package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import kotlin.math.floor

class StripedPattern(
    val first: COLOR,
    val second: COLOR,
) : Pattern() {
    override fun at(point: POINT): COLOR = if (floor(point.x) % 2.0f == .0f) first else second
}