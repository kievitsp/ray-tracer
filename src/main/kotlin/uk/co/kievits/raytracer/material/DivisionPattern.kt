package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT

class DivisionPattern(
    private val base: Pattern,
    private val value: Float,
) : Pattern() {
    override fun atPattern(shapePoint: POINT): COLOR = base.atPattern(shapePoint) / value
}
