package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.Vector
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Cone : CylinderLike() {
    override fun localNormalAt(p: POINT): TUPLE {
        val (px, py, pz) = p

        val y = sqrt(px.pow(2) + pz.pow(2))

        return when {
            py > 0 -> Vector(px, -y, pz)
            else -> Vector(px, y, pz)
        }
    }

    override fun localIntersections(ray: Ray): Intersections {
        val (dx, dy, dz) = ray.direction
        val (ox, oy, oz) = ray.origin

        val a = dx.pow(2) -
            dy.pow(2) +
            dz.pow(2)

        val b = 2 * ox * dx -
            2 * oy * dy +
            2 * oz * dz

        val c = ox.pow(2) -
            oy.pow(2) +
            oz.pow(2)

        val xs: MutableList<Intersection> = mutableListOf()

        when {
            abs(a) < EPSILON && abs(b) >= EPSILON -> xs.add(Intersection(-c / (2 * b), this))
            else -> calculateBodyIntersects(b, a, c, oy, dy, xs)
        }

        if (closed) intersectCaps(ray, xs)

        return Intersections(xs)
    }

    override fun checkCaps(
        ray: Ray,
        t: Double
    ): Boolean {
        val (ox, oy, oz) = ray.origin
        val (dx, dy, dz) = ray.direction

        val x = ox + t * dx
        val z = oz + t * dz
        val y = oy + t * dy

        return x.pow(2) + z.pow(2) <= abs(y)
    }
}
