package uk.co.kievits.raytracer.base

import jdk.incubator.vector.FloatVector
import jdk.incubator.vector.VectorMask
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorShuffle
import jdk.incubator.vector.VectorSpecies
import java.lang.StringBuilder

class Matrix<D : Dimension> private constructor(
    private val vector: FloatVector,
    private val dimension: D,
) {
    init {
        require(vector.species() == dimension.species) { vector.toArray().contentToString() }
    }

    constructor(
        array: FloatArray,
        dimension: D,
    ) : this(
        FloatVector.fromArray(dimension.species, array, 0),
        dimension,
    )

    val inverse by lazy(LazyThreadSafetyMode.NONE) {
        val new = FloatArray(dimension.size) {
            val (row, column) = dimension.rowAndColumn(it)
            cofactor(row, column)
        }

        FloatVector.fromArray(dimension.species, new, 0)
            .rearrange(dimension.transposeShuffle)
            .div(determinant())
            .toMatrix()
    }

    val transpose: Matrix<D> by lazy(LazyThreadSafetyMode.NONE) {
        vector.rearrange(dimension.transposeShuffle)
            .toMatrix()
    }

    infix fun napprox(other: Matrix<*>): Boolean = !approx(other)

    infix fun approx(other: Matrix<*>): Boolean {
        val diff = vector.sub(other.vector)
            .abs()
            .reduceLanes(VectorOperators.MAX)

        return diff < EPSILON
    }

    operator fun get(x: Int, y: Int): Float {
        require(x in 0 until dimension.width && y in 0 until dimension.width)
        return vector.lane(dimension.index(x, y))
    }

    private fun row(row: Int): FloatVector = vector.rearrange(dimension.rowShuffles[row])
        .toSpecies(FloatTuple.SPECIES)

    private fun rows() = Array(dimension.width) { rowId -> row(rowId) }

    private fun column(column: Int): FloatVector = vector.rearrange(dimension.columnShuffles[column])
        .toSpecies(FloatTuple.SPECIES)

    private fun columns() = buildList {
        for (columnId in 0 until dimension.width) {
            add(column(columnId))
        }
    }

//    fun inverse(): Matrix<D> {
//        assert(isInvertable())
//
//        val new = FloatArray(dimension.size) {
//            val (row, column) = dimension.rowAndColumn(it)
//            cofactor(row, column)
//        }
//
//        return FloatVector.fromArray(dimension.species, new, 0)
//            .rearrange(dimension.transposeShuffle)
//            .div(determinant())
//            .toMatrix()
//    }

    operator fun times(other: Matrix<D>): Matrix<D> {
        assert(dimension == other.dimension) { "$dimension vs ${other.dimension}" }
        var columns = other.transpose.vector
        val array = FloatArray(dimension.size)
        val width = dimension.width
        for (i in 0 until width) {
            val result = vector * columns

            val value0 = result.reduceLanes(VectorOperators.ADD, mask0)
            val value1 = result.reduceLanes(VectorOperators.ADD, mask1)
            val value2 = result.reduceLanes(VectorOperators.ADD, mask2)
            val value3 = result.reduceLanes(VectorOperators.ADD, mask3)

            array[D4.index(0, (0 + i) % width)] = value0
            array[D4.index(1, (1 + i) % width)] = value1
            array[D4.index(2, (2 + i) % width)] = value2
            array[D4.index(3, (3 + i) % width)] = value3

            columns = columns.rearrange(jump)
        }

        return Matrix(array, dimension)
    }

    operator fun times(other: FloatTuple): FloatTuple {
        assert(dimension.width == 4) { "$dimension" }
        val column = FloatVector.fromArray(D4.species, FloatArray(16) { other.vector.lane(it % 4) }, 0)

        val result = vector * column

        val value0 = result.reduceLanes(VectorOperators.ADD, mask0)
        val value1 = result.reduceLanes(VectorOperators.ADD, mask1)
        val value2 = result.reduceLanes(VectorOperators.ADD, mask2)
        val value3 = result.reduceLanes(VectorOperators.ADD, mask3)

        val array = FloatArray(4)

        array[0] = value0
        array[1] = value1
        array[2] = value2
        array[3] = value3

        return FloatTuple(array)
    }

    operator fun times(other: DoubleTuple): DoubleTuple {
        assert(dimension.width == 4) { "$dimension" }
        val column = FloatVector.fromArray(D4.species, FloatArray(16) { other.vector.lane(it % 4).toFloat() }, 0)

        val result = (vector * column)

        val value0 = result.reduceLanes(VectorOperators.ADD, mask0)
        val value1 = result.reduceLanes(VectorOperators.ADD, mask1)
        val value2 = result.reduceLanes(VectorOperators.ADD, mask2)
        val value3 = result.reduceLanes(VectorOperators.ADD, mask3)

        val array = DoubleArray(4)

        array[0] = value0.toDouble()
        array[1] = value1.toDouble()
        array[2] = value2.toDouble()
        array[3] = value3.toDouble()

        return DoubleTuple(array)
    }

    fun subMatrix(row: Int, column: Int): Matrix<*> {
        val sub = dimension.sub ?: throw UnsupportedOperationException()
        val vectorShuffle = dimension.subArrayShuffle[row][column]
        val species = sub.species

        return Matrix(
            vector.rearrange(vectorShuffle, dimension.subMask)
                .toSpecies(species),
            sub
        )
    }

    fun minor(x: Int, y: Int): Float = subMatrix(x, y).determinant()
    fun cofactor(x: Int, y: Int): Float {
        val determinant = subMatrix(x, y).determinant()
        return if ((x + y) % 2 == 0) determinant else -determinant
    }

    fun isInvertable(): Boolean = determinant() != 0.0f

    fun determinant(): Float = when (dimension) {
        is D2 -> this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]
        else -> {
            val row = row(0)
            val floatArray = FloatArray(4) {
                when {
                    it < dimension.width -> cofactor(0, it)
                    else -> 0f
                }
            }
            val factors = FloatVector.fromArray(FloatTuple.SPECIES, floatArray, 0)
            row.mul(factors).reduceLanes(VectorOperators.ADD)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix<*>) return false
        return this approx other
    }

    override fun toString(): String = StringBuilder().apply {
        for (index in 0 until dimension.size) {
            if (index == 0) append("{")
            append(vector.lane(index))
            if (index == dimension.size - 1) append("}")
            else if ((index + 1) % dimension.width == 0) append("\n")
            else append(", ")
        }
    }.toString()

    private fun FloatVector.toMatrix() = Matrix(this, dimension)

    companion object {
        fun D2(array: FloatArray): Matrix<D2> = Matrix(array, D2)
        fun D3(array: FloatArray): Matrix<D3> {
            val newArray = FloatArray(D3.species.length())
            System.arraycopy(array, 0, newArray, 0, array.size)

            return Matrix(newArray, D3)
        }

        fun D4(vararg array: Float): Matrix<D4> = Matrix(array, D4)

        fun D4(vararg array: Number): Matrix<D4> = Matrix(FloatArray(16) { array[it].toFloat() }, D4)

        private val mask0 = buildVectorMask(0)
        private val mask1 = buildVectorMask(1)
        private val mask2 = buildVectorMask(2)
        private val mask3 = buildVectorMask(3)

        private val jump = VectorShuffle.fromArray(
            D4.species,
            intArrayOf(
                4, 5, 6, 7,
                8, 9, 10, 11,
                12, 13, 14, 15,
                0, 1, 2, 3,
            ),
            0
        )

        fun buildVectorMask(id: Int): VectorMask<Float> {
            val min = id * 4
            val max = (id + 1) * 4
            return VectorMask.fromArray(D4.species, BooleanArray(16) { id -> id in min until max }, 0)
        }

        init {
        }
    }
}

fun FloatVector.toSpecies(newSpecies: VectorSpecies<Float>): FloatVector {
    if (species() == newSpecies) return this
    val array = FloatArray(maxOf(species().length(), newSpecies.length()))
    intoArray(array, 0)
    return FloatVector.fromArray(newSpecies, array, 0)
}
