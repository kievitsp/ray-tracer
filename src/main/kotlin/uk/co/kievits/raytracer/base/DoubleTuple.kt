package uk.co.kievits.raytracer.base

import jdk.incubator.vector.DoubleVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorShuffle
import kotlin.math.sqrt

class DoubleTuple(
    val vector: DoubleVector,
) {
    init {
        assert(vector.species() == SPECIES) { vector }
    }

    fun copy(
        x: Double = this.x,
        y: Double = this.y,
        z: Double = this.z,
        w: Double = this.w,
    ) = DoubleTuple(doubleArrayOf(x, y, z, w))

    constructor(array: DoubleArray) : this(DoubleVector.fromArray(SPECIES, array, 0))

    constructor(x: Number, y: Number, z: Number, w: Number) : this(
        doubleArrayOf(
            x.toDouble(),
            y.toDouble(),
            z.toDouble(),
            w.toDouble()
        )
    )

    val x: Double get() = vector.lane(0)
    val y: Double get() = vector.lane(1)
    val z: Double get() = vector.lane(2)
    val w: Double get() = vector.lane(3)

    val red: Double get() = vector.lane(0)
    val green: Double get() = vector.lane(1)
    val blue: Double get() = vector.lane(2)

    operator fun component1(): Double = x
    operator fun component2(): Double = y
    operator fun component3(): Double = z
    operator fun component4(): Double = w

    val isPoint: Boolean get() = w == POINT_W
    val isVector: Boolean get() = !isPoint

    val magnitude: Double
        get() = sqrt(
            vector
                .pow(2.0)
                .reduceLanes(VectorOperators.ADD)
        )

    val normalise: DoubleTuple get() = this / magnitude

    operator fun plus(other: DoubleTuple): DoubleTuple = (vector + other.vector).toTuple()
    operator fun minus(other: DoubleTuple): DoubleTuple = (vector - other.vector).toTuple()
    operator fun times(other: DoubleTuple): DoubleTuple = (vector * other.vector).toTuple()
    operator fun div(other: DoubleTuple): DoubleTuple = (vector / other.vector).toTuple()

    operator fun plus(other: Double): DoubleTuple = (vector + other).toTuple()
    operator fun minus(other: Double): DoubleTuple = (vector - other).toTuple()
    operator fun times(other: Double): DoubleTuple = (vector * other).toTuple()
    operator fun div(other: Double): DoubleTuple = (vector / other).toTuple()

    operator fun unaryMinus(): DoubleTuple = (-vector).toTuple()

    infix fun dot(other: DoubleTuple): Double = vector dot other.vector

    infix fun cross(other: DoubleTuple): DoubleTuple {
        val a = vector
        val b = other.vector

        val first = a.rearrange(yzxShuffle)
            .mul(b.rearrange(zxyShuffle))

        val second = a.rearrange(zxyShuffle)
            .mul(b.rearrange(yzxShuffle))

        return first.sub(second).toTuple()
    }

    private fun DoubleVector.toTuple() = DoubleTuple(this)

    infix fun approx(other: DoubleTuple): Boolean {
        val maxDiff = vector.sub(other.vector)
            .abs()
            .reduceLanes(VectorOperators.MAX)

        return maxDiff < (EPSILON * 2)
    }

    infix fun reflect(normal: DoubleTuple): DoubleTuple = this - (normal * 2.0 * (this dot normal))

    override fun toString(): String = vector.toArray().contentToString()

    override fun equals(other: Any?): Boolean {
        if (other !is DoubleTuple) return false
        return this.approx(other)
    }

    override fun hashCode(): Int = vector.hashCode()

    companion object {
        internal val SPECIES = DoubleVector.SPECIES_256

        private val yzxShuffle = VectorShuffle.fromArray(SPECIES, intArrayOf(1, 2, 0, 3), 0)
        private val zxyShuffle = VectorShuffle.fromArray(SPECIES, intArrayOf(2, 0, 1, 3), 0)

        const val POINT_W: Double = 1.0
        const val VECTOR_W: Double = 0.0

        const val EPSILON: Double = 0.00001
    }
}

operator fun DoubleVector.plus(other: DoubleVector) = add(other)
operator fun DoubleVector.minus(other: DoubleVector) = sub(other)
operator fun DoubleVector.times(other: DoubleVector) = mul(other)
operator fun DoubleVector.div(other: DoubleVector) = div(other)

infix fun DoubleVector.dot(other: DoubleVector): Double =
    mul(other).reduceLanes(VectorOperators.ADD)

operator fun DoubleVector.plus(other: Double) = add(other)
operator fun DoubleVector.minus(other: Double) = sub(other)
operator fun DoubleVector.times(other: Double) = mul(other)
operator fun DoubleVector.div(other: Double) = div(other)

operator fun DoubleVector.unaryMinus() = neg()
