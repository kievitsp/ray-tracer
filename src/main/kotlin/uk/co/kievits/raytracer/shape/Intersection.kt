package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.model.Sphere

data class Intersection(
    val t: V,
    val shape: Sphere,
) {
    fun precompute(ray: Ray): PartialResults {
        val point = ray.position(t)
        val eyeV = -ray.direction
        val normalV = shape.normalAt(point)
        val normalVDotEyeV = normalV dot eyeV
        val isInside = normalVDotEyeV < 0
        val realNormalV = if (isInside) -normalV else normalV

        return PartialResults(
            t = t,
            shape = shape,
            point = point,
            eyeV = eyeV,
            normalV = realNormalV,
            isInside = isInside,
            overPoint = point + (realNormalV * EPSILON)
        )
    }
}
