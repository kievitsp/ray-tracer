package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.V
import kotlin.math.pow
import kotlin.math.sqrt

abstract class CylinderLike : Shape() {
    var minimum: V = V.NEGATIVE_INFINITY
    var maximum: V = V.POSITIVE_INFINITY
    var closed: Boolean = false

    protected fun calculateBodyIntersects(
        b: V,
        a: V,
        c: V,
        oy: V,
        dy: V,
        xs: MutableList<Intersection>
    ) {
        var disc = b.pow(2) - 4 * a * c
        if (disc in -EPSILON..0.0) disc = 0.0
        if (disc >= 0) {
            val t0 = (-b - sqrt(disc)) / (2 * a)
            val t1 = (-b + sqrt(disc)) / (2 * a)

            val y0 = oy + t0 * dy
            val y1 = oy + t1 * dy

            if (y0 > minimum && y0 < maximum) xs.add(Intersection(t0, this))
            if (y1 > minimum && y1 < maximum) xs.add(Intersection(t1, this))
        }
    }

    protected abstract fun checkCaps(ray: Ray, t: Double): Boolean

    protected fun intersectCaps(
        ray: Ray,
        xs: MutableList<Intersection>
    ) {
        val tMin = (minimum - ray.origin.y) / ray.direction.y
        val tMax = (maximum - ray.origin.y) / ray.direction.y

        if (checkCaps(ray, tMin)) xs.add(Intersection(tMin, this))
        if (checkCaps(ray, tMax)) xs.add(Intersection(tMax, this))
    }
}
