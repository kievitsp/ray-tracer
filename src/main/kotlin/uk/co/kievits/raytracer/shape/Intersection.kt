package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.model.Sphere

data class Intersection(
    val t: V,
    val shape: Sphere,
) {
    fun precompute(ray: Ray): PartialResults {
        val point = ray.position(t)
        return PartialResults(
            t = t,
            shape = shape,
            point = point,
            eyeV = -ray.direction,
            normalV = shape.normalAt(point),
        )
    }
}
