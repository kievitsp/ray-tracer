package uk.co.kievits.raytracer.base

import jdk.incubator.vector.FloatVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorShuffle
import kotlin.math.sqrt

class Tuple(
    val vector: FloatVector,
) {
    init {
        require(vector.species() == SPECIES) { vector }
    }

    constructor(array: FloatArray) : this(FloatVector.fromArray(SPECIES, array, 0))

    val x: V get() = vector.lane(0)
    val y: V get() = vector.lane(1)
    val z: V get() = vector.lane(2)
    val w: V get() = vector.lane(3)
    val red: V get() = vector.lane(0)
    val green: V get() = vector.lane(1)
    val blue: V get() = vector.lane(2)

    val isPoint: Boolean get() = w == POINT_W
    val isVector: Boolean get() = !isPoint

    val magnitude: V
        get() = sqrt(
            vector
                .pow(2f)
                .reduceLanes(VectorOperators.ADD)
        )

    val normalise: Tuple get() = this / magnitude

    constructor(x: Number, y: Number, z: Number, w: Number) : this(
        floatArrayOf(
            x.toFloat(),
            y.toFloat(),
            z.toFloat(),
            w.toFloat()
        )
    )

    operator fun plus(other: Tuple): Tuple = (vector + other.vector).toTuple()
    operator fun minus(other: Tuple): Tuple = (vector - other.vector).toTuple()
    operator fun times(other: Tuple): Tuple = (vector * other.vector).toTuple()
    operator fun div(other: Tuple): Tuple = (vector / other.vector).toTuple()

    operator fun plus(other: V): Tuple = (vector + other).toTuple()
    operator fun minus(other: V): Tuple = (vector - other).toTuple()
    operator fun times(other: V): Tuple = (vector * other).toTuple()
    operator fun div(other: V): Tuple = (vector / other).toTuple()

    operator fun unaryMinus(): Tuple = (-vector).toTuple()

    infix fun dot(other: Tuple): Float = vector dot other.vector

    infix fun cross(other: Tuple): Tuple {
        val a = vector
        val b = other.vector

        val first = a.rearrange(yzxShuffle)
            .mul(b.rearrange(zxyShuffle))

        val second = a.rearrange(zxyShuffle)
            .mul(b.rearrange(yzxShuffle))

        return first.sub(second).toTuple()
    }

    infix fun approx(other: Tuple): Boolean {
        val dif = vector.sub(other.vector)
            .abs()
            .reduceLanes(VectorOperators.MAX)

        return dif < EPSILON
    }

    private fun FloatVector.toTuple() = Tuple(this)

    fun copy() = Tuple(vector.toArray())

    override fun toString(): String = vector.toArray().contentToString()

    override fun equals(other: Any?): Boolean {
        if (other !is Tuple) return false
        return this.approx(other)
    }

    override fun hashCode(): Int = vector.hashCode()

    companion object {
        internal val SPECIES = FloatVector.SPECIES_128

        private val yzxShuffle = VectorShuffle.fromArray(SPECIES, intArrayOf(1, 2, 0, 3), 0)
        private val zxyShuffle = VectorShuffle.fromArray(SPECIES, intArrayOf(2, 0, 1, 3), 0)
    }
}

operator fun FloatVector.plus(other: FloatVector) = add(other)
operator fun FloatVector.minus(other: FloatVector) = sub(other)
operator fun FloatVector.times(other: FloatVector) = mul(other)
operator fun FloatVector.div(other: FloatVector) = div(other)

infix fun FloatVector.dot(other: FloatVector): Float =
    mul(other).reduceLanes(VectorOperators.ADD)

operator fun FloatVector.plus(other: Float) = add(other)
operator fun FloatVector.minus(other: Float) = sub(other)
operator fun FloatVector.times(other: Float) = mul(other)
operator fun FloatVector.div(other: Float) = div(other)

operator fun FloatVector.unaryMinus() = neg()
