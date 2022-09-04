package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import kotlin.math.floor

class CheckeredPattern(
    val first: COLOR,
    val second: COLOR,
) : Pattern() {
    override fun at(point: POINT): COLOR {
        val check = floor(point.x) + floor(point.y) + floor(point.z)
        return if (floor(check) % 2.0f == .0f) first else second
    }
}
