package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.material.Material

abstract class WorldAware(
    var transform: Matrix<D4>,
)

abstract class Shape(
    transform: Matrix<D4> = IdentityMatrix(),
    var material: Material = Material(),
) : WorldAware(transform) {

    fun normalAt(
        worldPoint: POINT,
    ): VECTOR {
        val objectPoint = transform.inverse * worldPoint
        val objectNormal = localNormalAt(objectPoint)
        val worldNormal = transform.inverse.transpose * objectNormal

        return worldNormal.copy(w = 0.0).normalise
    }

    abstract fun localNormalAt(p: POINT): TUPLE

    fun intersections(ray: Ray): Intersections {
        val localRay = ray.transform(transform.inverse)
        return localIntersections(localRay)
    }

    abstract fun localIntersections(ray: Ray): Intersections
}
