package uk.co.kievits.raytracer.model

import org.jetbrains.kotlinx.multik.api.*
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

typealias V = Double
typealias TUPLE = D1Array<V>
typealias MATRIX = D2Array<V>
typealias CANVAS = Canvas
typealias COLOR = TUPLE
typealias VECTOR = TUPLE
typealias POINT = TUPLE

const val POINT_W: V = 1.0
const val VECTOR_W: V = 0.0
const val EPSILON: V = 0.00001

inline val TUPLE.x: V get() = this[0]
inline val TUPLE.y: V get() = this[1]
inline val TUPLE.z: V get() = this[2]
inline val TUPLE.w: V get() = this[3]

inline val TUPLE.red: V get() = this[0]
inline val TUPLE.green: V get() = this[1]
inline val TUPLE.blue: V get() = this[2]

//inline val CANVAS.height: Int get() = this.shape[0]
//inline val CANVAS.width: Int get() = this.shape[1]

infix fun V.approx(other: V): Boolean {
    val diff = abs(this - other)
    return diff < EPSILON
}

operator fun MATRIX.times(tuple: TUPLE): TUPLE = Tuple(
    this[0].foldIndexed(0.0) { index, acc, d -> acc + (tuple[index] * d) },
    this[1].foldIndexed(0.0) { index, acc, d -> acc + (tuple[index] * d) },
    this[2].foldIndexed(0.0) { index, acc, d -> acc + (tuple[index] * d) },
    this[3].foldIndexed(0.0) { index, acc, d -> acc + (tuple[index] * d) },
)

@JvmName("times2")
operator fun MATRIX.times(b: MATRIX): MATRIX {
    assert(shape.contentEquals(b.shape))
    val height = shape[0]
    val width = shape[1]
    val new = mk.d2array(height, width) { 0.0 }
    val a = this

    for (x in 0 until height) {
        for (y in 0 until height) {
            new[x, y] = a[x, 0] * b[0, y] +
                    a[x, 1] * b[1, y] +
                    a[x, 2] * b[2, y] +
                    a[x, 3] * b[3, y]
        }
    }

    return new
}


infix fun MultiArray<V, D1>.approx(other: MultiArray<V, D1>): Boolean {
    require(this.size == other.size)
    indices.forEach { idx ->
        if (this[idx] approx other[idx]) return@forEach
        return false
    }
    return true
}

@JvmName("approx2")
infix fun MATRIX.approx(other: MATRIX): Boolean {
    require(this.size == other.size)
    data.indices.forEach { idx ->
        if (this[idx] approx other[idx]) return@forEach
        return false
    }
    return true
}

inline val TUPLE.isPoint: Boolean get() = w == POINT_W
inline val TUPLE.isVector: Boolean get() = !isPoint

inline val TUPLE.magnitude: V
    get() = sqrt(
        x.pow(2) +
                y.pow(2) +
                z.pow(2) +
                w.pow(2)
    )

inline val TUPLE.normalise: TUPLE get() = this / this.magnitude

inline val Number.asV get() = toDouble()

infix fun VECTOR.cross(b: VECTOR): VECTOR {
    val a = this
    require(a.w == 0.0 && b.w == 0.0)
    val x = a * b
    return Vector(
        a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x,
    )
}


fun MATRIX.determinant(): Double {
    require(dim.d == 2) { dim.d }
    return when {
        shape.contentEquals(intArrayOf(2, 2)) -> {
            this[0, 0] * this[1, 1] - this[0, 1] * this[1, 0]
        }

        else -> {
            val array = mk.d1array(shape[0]) { this[0].data[it] }
            array.foldIndexed(0.0) { index, acc, d ->
                acc + d * this.cofactor(0, index)
            }
        }
    }
}

fun MATRIX.inverse(): MATRIX {
    var new = this.copy()
    val height = shape[0]
    val width = shape[1]

    for (x in 0 until height) {
        for (y in 0 until width) {
            new[x, y] = cofactor(x, y)
        }
    }
    new = new.transpose()
    val determinant = determinant()

    return new.map { it / determinant }
}

fun MATRIX.isInvertable(): Boolean = determinant() != 0.0
fun MATRIX.minor(x: Int, y: Int): Double = subMatrix(x, y).determinant()
fun MATRIX.cofactor(x: Int, y: Int): Double {
    val factor = if (x + y % 2 == 0) 1 else -1
    return subMatrix(x, y).determinant() * factor
}

fun MATRIX.subMatrix(row: Int, column: Int): MATRIX {
    val array: MultiArray<V, D1> = this[row]
    val height = shape[0]
    val width = shape[1]
    array[0..width]

    val new = mk.d2array(height - 1, width - 1) { 0.0 }

    for (r in 0 until row) {
        for (c in 0 until column) {
            new[r, c] = this[r, c]
        }
        for (c in (column + 1) until width) {
            new[r, c - 1] = this[r, c]
        }
    }

    for (r in (row + 1) until height) {
        for (c in 0 until column) {
            new[r - 1, c] = this[r, c]
        }
        for (c in (column + 1) until width) {
            new[r - 1, c - 1] = this[r, c]
        }
    }

    return new
}

fun ZeroPoint() = Point(0, 0, 0)
fun ZeroVector() = Vector(0, 0, 0)
fun CANVAS.toPpm(): String {
    TODO()
}

fun Point(x: Number, y: Number, z: Number) = Point(x.asV, y.asV, z.asV)
fun Point(x: V, y: V, z: V) = Tuple(x, y, z, POINT_W)

fun Color(red: Number, green: Number, blue: Number) = Color(red.asV, green.asV, blue.asV)
fun Color(red: V, green: V, blue: V) = mk.ndarray(mk[red, green, blue])

fun Vector(x: Number, y: Number, z: Number) = Vector(x.asV, y.asV, z.asV)
fun Vector(x: V, y: V, z: V) = Tuple(x, y, z, VECTOR_W)

fun Tuple(x: Number, y: Number, z: Number, w: Number) = Tuple(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun Tuple(x: V, y: V, z: V, w: V) = mk.ndarray(mk[x, y, z, w])

fun IdentityMatrix() = mk.identity<Double>(4)

object Colors {
    val BLACK get() = Color(0, 0, 0)
    val RED get() = Color(0, 0, 0)
}