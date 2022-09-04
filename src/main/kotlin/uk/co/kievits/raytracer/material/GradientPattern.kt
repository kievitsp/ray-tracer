package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import kotlin.math.floor

class GradientPattern(
    val first: COLOR,
    val second: COLOR,
) : Pattern() {
    override fun at(point: POINT): COLOR = first + (second - first) * (point.x - floor(point.x))
}
