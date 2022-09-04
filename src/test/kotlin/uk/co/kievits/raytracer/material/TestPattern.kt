package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.POINT

class TestPattern : Pattern(IdentityMatrix()) {
    override fun at(point: POINT): COLOR = Color(point.x, point.y, point.z)
}
