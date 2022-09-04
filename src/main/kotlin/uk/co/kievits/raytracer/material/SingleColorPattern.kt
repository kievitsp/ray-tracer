package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.shape.Shape

data class SingleColorPattern(
    val color: COLOR,
) : Pattern() {
    override fun at(point: POINT): COLOR = color

    override fun atShape(shape: Shape, point: POINT): COLOR = color
}
