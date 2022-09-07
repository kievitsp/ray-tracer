package uk.co.kievits.raytracer.base

import uk.co.kievits.raytracer.canvas.PpmCanvas
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

typealias V = Double
typealias TUPLE = DoubleTuple
typealias MATRIX = Matrix<*>
typealias CANVAS = PpmCanvas
typealias COLOR = TUPLE
typealias VECTOR = TUPLE
typealias POINT = TUPLE

const val EPSILON: V = TUPLE.EPSILON

fun PointZero() = Point(0f, 0f, 0f)
fun Point(x: Number, y: Number, z: Number) = TUPLE(x, y, z, TUPLE.POINT_W)
fun Point(x: V, y: V, z: V) = TUPLE(x, y, z, TUPLE.POINT_W)
fun Vector(x: Number, y: Number, z: Number) = TUPLE(x, y, z, TUPLE.VECTOR_W)
fun Color(red: Number, green: Number, blue: Number) = COLOR(red, green, blue, 0)

fun Number.toV(): V = toDouble()

infix fun Double.approx(other: Double): Boolean {
    val diff = abs(this - other)
    return diff < (2 * EPSILON)
}

infix fun Float.approx(other: Float): Boolean {
    val diff = abs(this - other)
    return diff < (2 * EPSILON)
}

fun translation(x: Number = 0, y: Number = 0, z: Number = 0) = translation(x.toFloat(), y.toFloat(), z.toFloat())
fun translation(x: Float, y: Float, z: Float) = Matrix.D4(
    1f, 0f, 0f, x,
    0f, 1f, 0f, y,
    0f, 0f, 1f, z,
    0f, 0f, 0f, 1f,
)

fun scaling(scale: Number) = scaling(scale, scale, scale)
fun scaling(x: Number = 0, y: Number = 0, z: Number = 0) = scaling(x.toFloat(), y.toFloat(), z.toFloat())

fun scaling(x: Float, y: Float, z: Float) = Matrix.D4(
    x, 0f, 0f, 0f,
    0f, y, 0f, 0f,
    0f, 0f, z, 0f,
    0f, 0f, 0f, 1f,
)

fun rotationX(r: Number) = rotationX(r.toFloat())

fun rotationX(r: Float) = Matrix.D4(
    1f, 0f, 0f, 0f,
    0f, cos(r), -sin(r), 0f,
    0f, sin(r), cos(r), 0f,
    0f, 0f, 0f, 1f,
)

fun rotationY(r: Number) = rotationY(r.toFloat())
fun rotationY(r: Float) = Matrix.D4(
    cos(r), 0f, sin(r), 0f,
    0f, 1f, 0f, 0f,
    -sin(r), 0f, cos(r), 0f,
    0f, 0f, 0f, 1f,
)

fun rotationZ(r: Number) = rotationZ(r.toFloat())
fun rotationZ(r: Float) = Matrix.D4(
    cos(r), -sin(r), 0f, 0f,
    sin(r), cos(r), 0f, 0f,
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f,
)

fun shearing(
    xy: Number,
    xz: Number,
    yx: Number,
    yz: Number,
    zx: Number,
    zy: Number,
) = shearing(
    xy = xy.toFloat(),
    xz = xz.toFloat(),
    yx = yx.toFloat(),
    yz = yz.toFloat(),
    zx = zx.toFloat(),
    zy = zy.toFloat(),
)

fun shearing(
    xy: Float,
    xz: Float,
    yx: Float,
    yz: Float,
    zx: Float,
    zy: Float,
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
