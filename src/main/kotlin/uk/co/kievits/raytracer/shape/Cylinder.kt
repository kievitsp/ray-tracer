package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.Vector
import kotlin.math.abs
import kotlin.math.pow

class Cylinder : CylinderLike() {
    override fun localNormalAt(p: POINT): TUPLE {
        val dist = p.x.pow(2) + p.z.pow(2)

        return when {
            dist < 1 && p.y >= maximum - EPSILON -> Vector(0, 1, 0)
            dist < 1 && p.y <= minimum + EPSILON -> Vector(0, -1, 0)
            else -> Vector(p.x, 0, p.z)
        }
    }

    override fun localIntersections(ray: Ray): Intersections {
        val (dx, _, dz, _) = ray.direction

        val a = dx.pow(2) + dz.pow(2)
        val xs: MutableList<Intersection> = mutableListOf()

        if (abs(a) > EPSILON) bodyIntersects(ray, a.toDouble(), xs)
        if (closed && abs(ray.direction.y) > EPSILON) intersectCaps(ray, xs)

        return Intersections(xs)
    }

    private fun bodyIntersects(
        ray: Ray,
        a: Double,
        xs: MutableList<Intersection>
    ) {
        val (dx, dy, dz, _) = ray.direction
        val (ox, oy, oz, _) = ray.origin

        val b = 2 * ox * dx + 2 * oz * dz
        val c = ox.pow(2) + oz.pow(2) - 1

        calculateBodyIntersects(b, a, c, oy, dy, xs)
    }

    override fun checkCaps(ray: Ray, t: Double): Boolean {
        val x = ray.origin.x + t * ray.direction.x
        val z = ray.origin.z + t * ray.direction.z

        return (x.pow(2) + z.pow(2)) <= 1f
    }
}
