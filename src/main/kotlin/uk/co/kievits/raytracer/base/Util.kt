package uk.co.kievits.raytracer.base

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
fun Point(x: Float, y: Float, z: Float) = Tuple(x, y, z, POINT_W)
fun Vector(x: Number, y: Number, z: Number) = Tuple(x, y, z, VECTOR_W)
fun Color(x: Number, y: Number, z: Number) = Tuple(x, y, z, 0)

infix fun V.approx(other: V): Boolean {
    val diff = abs(this - other)
    return diff < EPSILON
}

fun translation(x: V, y: V, z: V) = Matrix.D4(
    1f, 0f, 0f, x,
    0f, 1f, 0f, y,
    0f, 0f, 1f, z,
    0f, 0f, 0f, 1f,
)

fun scaling(x: V, y: V, z: V) = Matrix.D4(
    x, 0f, 0f, 0f,
    0f, y, 0f, 0f,
    0f, 0f, z, 0f,
    0f, 0f, 0f, 1f,
)

fun rotationX(r: V) = Matrix.D4(
    1f, 0f, 0f, 0f,
    0f, cos(r), -sin(r), 0f,
    0f, sin(r), cos(r), 0f,
    0f, 0f, 0f, 1f,
)

fun rotationY(r: V) = Matrix.D4(
    cos(r), 0f, sin(r), 0f,
    0f, 1f, 0f, 0f,
    -sin(r), 0f, cos(r), 0f,
    0f, 0f, 0f, 1f,
)

fun rotationZ(r: V) = Matrix.D4(
    cos(r), -sin(r), 0f, 0f,
    sin(r), cos(r), 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f,
)

fun shearing(
    xy: V,
    xz: V,
    yx: V,
    yz: V,
    zx: V,
    zy: V,
) = Matrix.D4(
    1f, xy, xz, 0f,
    yx, 1f, yz, 0f,
    zx, zy, 1f, 0f,
    0f, 0f, 0f, 1f,
)

fun IdentityMatrix() = Matrix.D4(
    1f, 0f, 0f, 0f,
    0f, 1f, 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f,
)

fun viewTransformation(
    from: POINT,
    to: POINT,
    up: VECTOR,
): Matrix<D4> {
    val forward = (to - from).normalise
    val left = forward cross up.normalise

    val trueUp = left cross forward
    val minusForward = -forward

    val orientation = Matrix.D4(
        left.x, left.y, left.z, 0f,
        trueUp.x, trueUp.y, trueUp.z, 0f,
        minusForward.x, minusForward.y, minusForward.z, 0f,
        0f, 0f, 0f, 1f,
    )

    val minusFrom = -from

    return orientation * translation(minusFrom.x, minusFrom.y, minusFrom.z)
}
