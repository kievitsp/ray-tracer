package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.Vector
import uk.co.kievits.raytracer.material.Material
import kotlin.math.abs

class Plane(
    transform: Matrix<D4> = IdentityMatrix(),
    material: Material = Material(),
) : Shape(transform, material) {
    override fun localNormalAt(p: POINT): TUPLE = NORMAL

    override fun localIntersections(ray: Ray): Intersections {
        if (abs(ray.direction.y) < EPSILON) return Intersections.Miss

        val t = -(ray.origin.y) / ray.direction.y
        return Intersections(t, this)
    }

    companion object {
        private val NORMAL = Vector(0, 1, 0)
    }
}
