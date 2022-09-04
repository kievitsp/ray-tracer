package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.Tuple
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.material.Material

abstract class Shape(
    transform: Matrix<D4>,
    var material: Material,
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
        val objectNormal = localNormalAt(objectPoint)
        val worldNormal = inverseTranspose * objectNormal

        return worldNormal.copy(w = 0f).normalise
    }

    abstract fun localNormalAt(p: POINT): Tuple

    fun intersections(ray: Ray): Intersections {
        val localRay = ray.transform(inverseTransform)
        return localIntersections(localRay)
    }

    abstract fun localIntersections(ray: Ray): Intersections
}
