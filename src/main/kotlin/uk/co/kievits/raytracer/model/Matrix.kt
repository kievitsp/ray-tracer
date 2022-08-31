package uk.co.kievits.raytracer.model

import jdk.incubator.vector.FloatVector
import jdk.incubator.vector.VectorOperators
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
        .toSpecies(Tuple.SPECIES)

    private fun column(column: Int): FloatVector = vector.rearrange(dimension.columnShuffles[column])
        .toSpecies(Tuple.SPECIES)

    fun inverse(): Matrix<D> {
        require(isInvertable())

        val new = FloatArray(dimension.size) {
            val (row, column) = dimension.rowAndColumn(it)
            cofactor(row, column)
        }
        val matrix = Matrix(new, dimension).transpose()
        val determinant = determinant()

        return matrix.vector
            .div(determinant)
            .toArray()
            .let { Matrix(it, dimension) }
    }

    fun transpose(): Matrix<D> = vector
        .rearrange(dimension.inversionShuffle)
        .toMatrix()

    private fun columns() = (0 until dimension.width).map { Tuple(column(it)) }


    operator fun times(other: Matrix<*>): Matrix<*> {
        require(dimension == other.dimension) { "$dimension vs ${other.dimension}" }
        val width = dimension.width
        val rows = rows()
        val columns = other.columns()

        val array = FloatArray(dimension.size) { id ->
            val row = id / width
            val column = id % width
            rows[row] dot columns[column]
        }
        return Matrix(array, dimension)
    }

    operator fun times(other: Tuple): Tuple {
        require(dimension.width == 4) { "$dimension" }
        val width = dimension.width
        val rows = rows()

        val array = FloatArray(dimension.width) { id ->
            rows[id] dot other
        }
        return Tuple(array)
    }

    private fun rows() = (0 until dimension.width).map {
        Tuple(FloatVector.fromArray(Tuple.SPECIES, row(it).toArray(), 0))
    }

    fun subMatrix(row: Int, column: Int): Matrix<*> {
        try {
            val vectorShuffle = dimension.subArrayShuffle[row][column]
            val sub = dimension.sub ?: throw UnsupportedOperationException()
            val species = sub.species

            return Matrix(
                vector.rearrange(vectorShuffle, dimension.subMask)
                    .toSpecies(species),
                sub
            )
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }

    fun minor(x: Int, y: Int): Float = subMatrix(x, y).determinant()
    fun cofactor(x: Int, y: Int): Float {
        val factor = if ((x + y) % 2 == 0) 1 else -1
        return subMatrix(x, y).determinant() * factor
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
            val factors = FloatVector.fromArray(Tuple.SPECIES, floatArray, 0)
            row.mul(factors).reduceLanes(VectorOperators.ADD)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Matrix<*>) return false
        return this approx other
    }

    override fun toString(): String {
        return StringBuilder().apply {
            for (index in 0 until dimension.size) {
                if (index == 0) append("{")
                append(vector.lane(index))
                if (index == dimension.size - 1) append("}")
                else if ((index + 1) % dimension.width == 0) append("\n")
                else append(", ")
            }
        }.toString()
    }

    private fun FloatVector.toMatrix() = Matrix(this, dimension)

    companion object {
        fun D2(array: FloatArray): Matrix<D2> = Matrix(array, D2)
        fun D3(array: FloatArray): Matrix<D3> {
            val newArray = FloatArray(D3.species.length())
            System.arraycopy(array, 0, newArray, 0, array.size)

            return Matrix(newArray, D3)
        }

        fun D4(array: FloatArray): Matrix<D4> = Matrix(array, D4)

    }
}

fun FloatVector.toSpecies(newSpecies: VectorSpecies<Float>): FloatVector {
    if (species() == newSpecies) return this
    val array = FloatArray(maxOf(species().length(), newSpecies.length()))
    intoArray(array, 0)
    return FloatVector.fromArray(newSpecies, array, 0)
}