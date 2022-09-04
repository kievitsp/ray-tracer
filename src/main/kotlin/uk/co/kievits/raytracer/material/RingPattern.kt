package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class RingPattern(
    val first: COLOR,
    val second: COLOR,
) : Pattern() {
    override fun at(point: POINT): COLOR {
        val distance = sqrt(point.x.pow(2) + point.z.pow(2))
        return if (floor(distance) % 2.0f == .0f) first else second
    }
}
