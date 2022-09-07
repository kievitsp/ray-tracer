package uk.co.kievits.raytracer.base

import jdk.incubator.vector.FloatVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorShuffle
import kotlin.math.sqrt

// @JvmInline
// value
@Deprecated("meh", level = DeprecationLevel.WARNING)
class FloatTuple(
    val vector: FloatVector,
) {
    init {
        assert(vector.species() == SPECIES) { vector }
    }

    fun copy(
        x: Float = this.x,
        y: Float = this.y,
        z: Float = this.z,
        w: Float = this.w,
    ) = FloatTuple(floatArrayOf(x, y, z, w))

    constructor(array: FloatArray) : this(FloatVector.fromArray(SPECIES, array, 0))

    val x: Float get() = vector.lane(0)
    val y: Float get() = vector.lane(1)
    val z: Float get() = vector.lane(2)
    val w: Float get() = vector.lane(3)
    val red: Float get() = vector.lane(0)
    val green: Float get() = vector.lane(1)
    val blue: Float get() = vector.lane(2)

    operator fun component1(): Float = x
    operator fun component2(): Float = y
    operator fun component3(): Float = z
    operator fun component4(): Float = w

    val isPoint: Boolean get() = w == POINT_W
    val isVector: Boolean get() = !isPoint

    val magnitude: Float
        get() = sqrt(
            vector
                .pow(2f)
                .reduceLanes(VectorOperators.ADD)
        )

    val normalise: FloatTuple get() = this / magnitude

    constructor(x: Number, y: Number, z: Number, w: Number) : this(
        floatArrayOf(
            x.toFloat(),
            y.toFloat(),
            z.toFloat(),
            w.toFloat()
        )
    )

    operator fun plus(other: FloatTuple): FloatTuple = (vector + other.vector).toTuple()
    operator fun minus(other: FloatTuple): FloatTuple = (vector - other.vector).toTuple()
    operator fun times(other: FloatTuple): FloatTuple = (vector * other.vector).toTuple()
    operator fun div(other: FloatTuple): FloatTuple = (vector / other.vector).toTuple()

    operator fun plus(other: Float): FloatTuple = (vector + other).toTuple()
    operator fun minus(other: Float): FloatTuple = (vector - other).toTuple()
    operator fun times(other: Float): FloatTuple = (vector * other).toTuple()
    operator fun div(other: Float): FloatTuple = (vector / other).toTuple()

    operator fun unaryMinus(): FloatTuple = (-vector).toTuple()

    infix fun dot(other: FloatTuple): Float = vector dot other.vector

    infix fun cross(other: FloatTuple): FloatTuple {
        val a = vector
        val b = other.vector

        val first = a.rearrange(yzxShuffle)
            .mul(b.rearrange(zxyShuffle))

        val second = a.rearrange(zxyShuffle)
            .mul(b.rearrange(yzxShuffle))

        return first.sub(second).toTuple()
    }

    infix fun approx(other: FloatTuple): Boolean {
        val maxDiff = vector.sub(other.vector)
            .abs()
            .reduceLanes(VectorOperators.MAX)

        return maxDiff < (EPSILON * 2)
    }

    infix fun reflect(normal: FloatTuple): FloatTuple = this - (normal * 2f * (this dot normal))

    private fun FloatVector.toTuple() = FloatTuple(this)

    fun copy() = FloatTuple(vector.toArray())

    override fun toString(): String = vector.toArray().contentToString()

    override fun equals(other: Any?): Boolean {
        if (other !is FloatTuple) return false
        return this.approx(other)
    }

    override fun hashCode(): Int = vector.hashCode()

    companion object {
        internal val SPECIES = FloatVector.SPECIES_128

        private val yzxShuffle = VectorShuffle.fromArray(SPECIES, intArrayOf(1, 2, 0, 3), 0)
        private val zxyShuffle = VectorShuffle.fromArray(SPECIES, intArrayOf(2, 0, 1, 3), 0)

        const val POINT_W: Float = 1.0f
        const val VECTOR_W: Float = 0.0f
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
