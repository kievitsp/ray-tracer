package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.PointZero
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.material.Material
import kotlin.math.pow
import kotlin.math.sqrt

class Sphere(
    transform: Matrix<D4> = IdentityMatrix(),
    material: Material = Material(),
) : Shape(transform, material) {

    override fun localNormalAt(p: POINT) = (p - Point(0, 0, 0)).normalise

    override fun localIntersections(ray: Ray): Intersections {
        val sphereToRay = ray.origin - PointZero()
        val a = ray.direction dot ray.direction
        val b = 2 * (ray.direction dot sphereToRay)
        val c = (sphereToRay dot sphereToRay) - 1

        val discrimant = b.pow(2) - 4 * a * c

        return when {
            discrimant < 0 -> Intersections.Miss
            else -> Intersections.Hits(
                hits = listOf(
                    Intersection((-b - sqrt(discrimant)) / (2 * a), this),
                    Intersection((-b + sqrt(discrimant)) / (2 * a), this),
                ),
                isSorted = true
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Sphere) return false

        return material == other.material &&
            transform approx other.transform
    }

    override fun toString(): String {
        return "Sphere(material=$material, transform=$transform)"
    }

    companion object {
        fun Glass(): Sphere = Sphere().apply {
            material.transparency = 1f
            material.refractiveIndex = 1.5f
        }
    }
}
