package uk.co.kievits.raytracer.model

import kotlin.math.pow
import kotlin.math.sqrt

class Sphere {

    fun intersections(ray: Ray): Intersections {
        val sphereToRay = ray.origin - PointZero()
        val a = ray.direction dot ray.direction
        val b = 2 * (ray.direction dot sphereToRay)
        val c = (sphereToRay dot sphereToRay) - 1

        val discrimant = b.pow(2) - 4 * a * c

        return when {
            discrimant < 0 -> Intersections.Miss
            else -> Intersections.Hit(
                t1 = (-b - sqrt(discrimant)) / (2 * a),
                t2 = (-b + sqrt(discrimant)) / (2 * a),
            )
        }
    }

}

sealed class Intersections {
    abstract val hits: Int

    object Miss : Intersections() {
        override val hits: Int
            get() = 0


    }

    data class Hit(
        val t1: V,
        val t2: V
    ) : Intersections() {
        override val hits: Int
            get() = 2


    }
}