package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.shape.Shape
import kotlin.math.floor

data class StripedPattern(
    val first: COLOR,
    val second: COLOR,
) : Pattern {
    override fun at(point: POINT): COLOR = if (floor(point.x) % 2.0f == .0f) first else second

    override fun atShape(shape: Shape, point: POINT): COLOR {
//        shape.localNormalAt()
        return if (floor(point.x) % 2.0f == .0f) first else second
    }
}

data class SingleColorPattern(
    val color: COLOR,
) : Pattern {
    override fun at(point: POINT): COLOR = color

    override fun atShape(shape: Shape, point: POINT): COLOR = color
}
