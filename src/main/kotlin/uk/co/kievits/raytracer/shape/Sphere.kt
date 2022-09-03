package uk.co.kievits.raytracer.model

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.PointZero
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.shape.Intersection
import uk.co.kievits.raytracer.shape.Intersections
import kotlin.math.pow
import kotlin.math.sqrt

class Sphere(
    transform: Matrix<D4> = IdentityMatrix(),
    var material: Material = Material(),
) {
    private var inverseTransform: Matrix<D4> = transform.inverse()
    private var inverseTranspose: Matrix<D4> = inverseTransform.transpose()

    var transform: Matrix<D4> = transform
        set(value) {
            field = value
            inverseTransform = value.inverse()
            inverseTranspose = inverseTransform.transpose()
        }

    fun normalAt(
        worldPoint: POINT,
    ): VECTOR {
        val objectPoint = inverseTransform * worldPoint
        val objectNormal = objectNormalAt(objectPoint)
        val worldNormal = inverseTranspose * objectNormal

        return worldNormal.copy(w = 0f).normalise
    }

    private fun objectNormalAt(p: POINT) = (p - Point(0, 0, 0)).normalise

    fun intersections(ray: Ray): Intersections = objectIntersections(ray.transform(inverseTransform))

    private fun objectIntersections(ray: Ray): Intersections {
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
}
