package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.shape.Shape

data class SolidPattern(
    val color: COLOR,
) : BasePattern() {
    override fun at(point: POINT): COLOR = color

    override fun atPattern(shapePoint: POINT): COLOR = color

    override fun atShape(shape: Shape, point: POINT): COLOR = color
}
