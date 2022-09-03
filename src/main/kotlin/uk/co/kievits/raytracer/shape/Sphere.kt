package uk.co.kievits.raytracer.model

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.PointZero
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.V
import kotlin.math.pow
import kotlin.math.sqrt

class Sphere(
    var transform: Matrix<D4> = IdentityMatrix()
) {

    fun intersections(ray: Ray): Intersections {
        val sphereToRay = ray.origin - PointZero()
        val a = ray.direction dot ray.direction
        val b = 2 * (ray.direction dot sphereToRay)
        val c = (sphereToRay dot sphereToRay) - 1

        val discrimant = b.pow(2) - 4 * a * c

        return when {
            discrimant < 0 -> Intersections.Miss
            else -> Intersections.Hits(
                listOf(
                    Intersection((-b - sqrt(discrimant)) / (2 * a), this),
                    Intersection((-b + sqrt(discrimant)) / (2 * a), this),
                )
            )
        }
    }
}

sealed class Intersections : List<Intersection> {
    object Miss : Intersections(), List<Intersection> by emptyList()

    data class Hits(
        val hits: List<Intersection>
    ) : Intersections(), List<Intersection> by hits
}

data class Intersection(
    val t: V,
    val shape: Sphere,
)
