package uk.co.kievits.raytracer.model

import jdk.incubator.vector.FloatVector
import jdk.incubator.vector.VectorOperators
import jdk.incubator.vector.VectorShuffle
import kotlin.math.abs
import kotlin.math.sqrt

typealias V = Float
typealias TUPLE = Tuple
typealias MATRIX = Matrix<*>
typealias CANVAS = Canvas
typealias COLOR = TUPLE
typealias VECTOR = TUPLE
typealias POINT = TUPLE

const val POINT_W: V = 1.0f
const val VECTOR_W: V = 0.0f
const val EPSILON: V = 0.00001f


fun Point(x: Number, y: Number, z: Number) = Tuple(x, y, z, POINT_W)
fun Vector(x: Number, y: Number, z: Number) = Tuple(x, y, z, VECTOR_W)
fun Color(x: Number, y: Number, z: Number) = Tuple(x, y, z, 0)

object Colors {
    val BLACK get() = Color(0, 0, 0)
    val RED get() = Color(0, 0, 0)
}

infix fun V.approx(other: V): Boolean {
    val diff = abs(this - other)
    return diff < EPSILON
}

typealias D2 = Dimension.D2
typealias D3 = Dimension.D3
typealias D4 = Dimension.D4

fun translation(x: V, y: V, z: V) = Matrix.D4(
    floatArrayOf(
        1f, 0f, 0f, x,
        0f, 1f, 0f, y,
        0f, 0f, 0f, z,
        0f, 0f, 0f, 1f,
    ),
)

fun translation(x: Number, y: Number, z: Number) = translation(x.toFloat(), y.toFloat(), z.toFloat())

fun IdentityMatrix(): Matrix<*> = Matrix.D4(
    floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    )
)

class Tuple(
    private val vector: FloatVector,
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
            toFloatVector()
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

    operator fun plus(other: Tuple): Tuple = toFloatVector()
        .add(other.toFloatVector())
        .toTuple()

    operator fun plus(other: V): Tuple = toFloatVector()
        .add(other)
        .toTuple()

    operator fun minus(other: Tuple): Tuple = toFloatVector()
        .sub(other.toFloatVector())
        .toTuple()

    operator fun minus(other: V): Tuple = toFloatVector()
        .sub(other)
        .toTuple()

    operator fun times(other: Tuple): Tuple = toFloatVector()
        .mul(other.toFloatVector())
        .toTuple()

    operator fun div(other: Tuple): Tuple = toFloatVector()
        .div(other.toFloatVector())
        .toTuple()

    operator fun div(other: V): Tuple = toFloatVector()
        .div(other)
        .toTuple()

    operator fun times(other: V): Tuple = toFloatVector()
        .mul(other)
        .toTuple()

    operator fun unaryMinus(): Tuple = toFloatVector()
        .neg()
        .toTuple()

    infix fun dot(other: Tuple): Float =
        toFloatVector()
            .mul(other.toFloatVector())
            .reduceLanes(VectorOperators.ADD)

    infix fun cross(other: Tuple): Tuple {

        val a = toFloatVector()
        val b = other.toFloatVector()

        val first = a.rearrange(yzxShuffle)
            .mul(b.rearrange(zxyShuffle))

        val second = a.rearrange(zxyShuffle)
            .mul(b.rearrange(yzxShuffle))

        return first.sub(second).toTuple()

//        require(a.w == 0.0f && other.w == 0.0f)
//        return Vector(
//            a.y * other.z - a.z * other.y,
//            a.z * other.x - a.x * other.z,
//            a.x * other.y - a.y * other.x,
//        )
    }

    infix fun approx(other: Tuple): Boolean {
        val dif = vector.sub(other.vector)
            .abs()
            .reduceLanes(VectorOperators.MAX)

        return dif < EPSILON
    }

    private fun toFloatVector() = vector
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