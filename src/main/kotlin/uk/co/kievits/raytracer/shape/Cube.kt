package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.Vector
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Cube : Shape() {
    override fun localNormalAt(p: POINT): TUPLE {
        val absX = abs(p.x)
        val absY = abs(p.y)
        val absZ = abs(p.z)

        return when (max(absX, max(absY, absZ))) {
            absX -> Vector(p.x, 0, 0)
            absY -> Vector(0, p.y, 0)
            absZ -> Vector(0, 0, p.z)
            else -> throw IllegalStateException()
        }
    }

    override fun localIntersections(ray: Ray): Intersections {
        val (xTmin, xTmax) = checkAxis(ray.origin.x, ray.direction.x)
        val (yTmin, yTmax) = checkAxis(ray.origin.y, ray.direction.y)
        val (zTmin, zTmax) = checkAxis(ray.origin.z, ray.direction.z)

        val tMin = max(xTmin, max(yTmin, zTmin))
        val tMax = min(xTmax, min(yTmax, zTmax))

        return when {
            tMin > tMax -> Intersections.Miss
            else -> Intersections(
                mutableListOf(
                    Intersection(tMin, this),
                    Intersection(tMax, this),
                ),
                true
            )
        }
    }

    private fun checkAxis(origin: V, direction: V): Pair<V, V> {
        val tminNumerator = (-1 - origin)
        val tmaxNumerator = (1 - origin)

        val hit = abs(direction) >= EPSILON
        val tmin = if (hit) tminNumerator / direction else tminNumerator * V.POSITIVE_INFINITY
        val tmax = if (hit) tmaxNumerator / direction else tmaxNumerator * V.POSITIVE_INFINITY

        return if (tmin > tmax) tmax to tmin else tmin to tmax
    }
}
