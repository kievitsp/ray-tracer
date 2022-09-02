package uk.co.kievits.raytracer.model

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

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

fun PointZero() = Point(0f, 0f, 0f)
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
        0f, 0f, 1f, z,
        0f, 0f, 0f, 1f,
    ),
)

fun scaling(x: V, y: V, z: V) = Matrix.D4(
    floatArrayOf(
        x, 0f, 0f, 0f,
        0f, y, 0f, 0f,
        0f, 0f, z, 0f,
        0f, 0f, 0f, 1f,
    ),
)

fun rotationX(r: V) = Matrix.D4(
    floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, cos(r), -sin(r), 0f,
        0f, sin(r), cos(r), 0f,
        0f, 0f, 0f, 1f,
    )
)

fun rotationY(r: V) = Matrix.D4(
    floatArrayOf(
        cos(r), 0f, sin(r), 0f,
        0f, 1f, 0f, 0f,
        -sin(r), 0f, cos(r), 0f,
        0f, 0f, 0f, 1f,
    )
)

fun rotationZ(r: V) = Matrix.D4(
    floatArrayOf(
        cos(r), -sin(r), 0f, 0f,
        sin(r), cos(r), 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    )
)

fun shearing(
    xy: V, xz: V,
    yx: V, yz: V,
    zx: V, zy: V,
) = Matrix.D4(
    floatArrayOf(
        1f, xy, xz, 0f,
        yx, 1f, yz, 0f,
        zx, zy, 1f, 0f,
        0f, 0f, 0f, 1f,
    )
)

fun IdentityMatrix(): Matrix<*> = Matrix.D4(
    floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f,
    )
)

