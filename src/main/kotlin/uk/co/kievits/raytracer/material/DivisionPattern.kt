package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.V

class DivisionPattern(
    private val base: Pattern,
    private val value: V,
) : Pattern() {
    override fun atPattern(shapePoint: POINT): COLOR = base.atPattern(shapePoint) / value
}
