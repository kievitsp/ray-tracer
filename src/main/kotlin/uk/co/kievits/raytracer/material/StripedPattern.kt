package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.shape.Shape
import kotlin.math.floor

class StripedPattern(
    val first: COLOR,
    val second: COLOR,
    transform: Matrix<D4> = IdentityMatrix(),
) : Pattern(transform) {
    override fun at(point: POINT): COLOR = if (floor(point.x) % 2.0f == .0f) first else second

    override fun atShape(shape: Shape, point: POINT): COLOR {
        val shapePoint = shape.inverseTranspose * point
        val patternPoint = inverseTransform * shapePoint

        return at(patternPoint)
    }
}

data class SingleColorPattern(
    val color: COLOR,
) : Pattern(IdentityMatrix()) {
    override fun at(point: POINT): COLOR = color

    override fun atShape(shape: Shape, point: POINT): COLOR = color
}
