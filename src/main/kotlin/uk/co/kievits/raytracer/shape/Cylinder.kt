package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.Tuple
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.Vector
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class Cylinder(
    var minimum: V = V.NEGATIVE_INFINITY,
    var maximum: V = V.POSITIVE_INFINITY,
    var closed: Boolean = false,
) : Shape() {
    override fun localNormalAt(p: POINT): Tuple {
        val dist = p.x.pow(2) + p.z.pow(2)

        return when {
            dist < 1 && p.y >= maximum - EPSILON -> Vector(0, 1, 0)
            dist < 1 && p.y <= minimum + EPSILON -> Vector(0, -1, 0)
            else -> Vector(p.x, 0, p.z)
        }
    }

    override fun localIntersections(ray: Ray): Intersections {
        val rdx = ray.direction.x.toDouble()
        val rdz = ray.direction.z.toDouble()

        val a = rdx.pow(2) + rdz.pow(2)
        val xs: MutableList<Intersection> = mutableListOf()

        if (abs(a) > EPSILON) bodyIntersects(ray, a, xs)
        if (closed && abs(ray.direction.y) > EPSILON) intersectCaps(ray, xs)

        return Intersections(xs)
    }

    private fun bodyIntersects(
        ray: Ray,
        a: Double,
        xs: MutableList<Intersection>
    ) {
        val rox = ray.origin.x.toDouble()
        val roz = ray.origin.z.toDouble()

        val rdx = ray.direction.x.toDouble()
        val rdz = ray.direction.z.toDouble()

        val roy = ray.origin.y.toDouble()
        val rdy = ray.direction.y.toDouble()

        val b = 2 * rox * rdx + 2 * roz * rdz

        val c = rox.pow(2) + roz.pow(2) - 1
        val disc = b.pow(2) - 4 * a * c

        if (disc < 0) return
        val t0 = (-b - sqrt(disc)) / (2 * a)
        val t1 = (-b + sqrt(disc)) / (2 * a)

        val y0 = roy + t0 * rdy
        val y1 = roy + t1 * rdy

        if (closed) {
            if (y0 in minimum..maximum) xs.add(Intersection(t0.toFloat(), this))
            if (y1 in minimum..maximum) xs.add(Intersection(t1.toFloat(), this))
        } else {
            if (y0 > minimum && y0 < maximum) xs.add(Intersection(t0.toFloat(), this))
            if (y1 > minimum && y1 < maximum) xs.add(Intersection(t1.toFloat(), this))
        }
    }

    private fun checkCaps(ray: Ray, t: V): Boolean {
        val x = ray.origin.x + t * ray.direction.x
        val z = ray.origin.z + t * ray.direction.z

        return (x.pow(2) + z.pow(2)) <= 1f
    }

    private fun intersectCaps(
        ray: Ray,
        xs: MutableList<Intersection>
    ) {
        val tMin = (minimum - ray.origin.y) / ray.direction.y
        val tMax = (maximum - ray.origin.y) / ray.direction.y

        if (checkCaps(ray, tMin)) xs.add(Intersection(tMin, this))
        if (checkCaps(ray, tMax)) xs.add(Intersection(tMax, this))
    }
}
