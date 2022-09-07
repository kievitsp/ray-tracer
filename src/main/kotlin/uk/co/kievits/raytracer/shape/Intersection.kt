package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.V

data class Intersection(
    val t: V,
    val shape: Shape,
) {
    fun precompute(
        ray: Ray,
        intersections: Intersections,
    ): PartialResults {
        val point = ray.position(t)
        val eyeV = -ray.direction
        val normalV = shape.normalAt(point)
        val normalVDotEyeV = normalV dot eyeV
        val isInside = normalVDotEyeV < 0
        val realNormalV = if (isInside) -normalV else normalV
        val normalEpsilon = realNormalV * EPSILON
        val reflectV = ray.direction.reflect(realNormalV)

        val containers = mutableListOf<Shape>()

        var n1: Float = -1f
        var n2: Float = -1f

        for (i in intersections) {
            if (i == this) {
                n1 = when {
                    containers.isEmpty() -> 1f
                    else -> containers.last().material.refractiveIndex
                }
            }
            when (i.shape) {
                in containers -> containers.remove(i.shape)
                else -> containers.add(i.shape)
            }
            if (i == this) {
                n2 = when {
                    containers.isEmpty() -> 1f
                    else -> containers.last().material.refractiveIndex
                }
            }
        }

        return PartialResults(
            t = t,
            shape = shape,
            point = point,
            eyeV = eyeV,
            normalV = realNormalV,
            isInside = isInside,
            overPoint = point + normalEpsilon,
            underPoint = point - normalEpsilon,
            reflectV = reflectV,
            n1 = n1,
            n2 = n2,
        )
    }
}
