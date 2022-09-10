package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.V

data class Intersection(
    val t: V,
    val shape: Shape,
) : Comparable<Intersection> {

    override fun compareTo(other: Intersection): Int = t.compareTo(other.t)

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

        var n1: V = -1.0
        var n2: V = -1.0

        for (i in intersections) {
            if (i == this) {
                n1 = when {
                    containers.isEmpty() -> 1.0
                    else -> containers.last().material.refractiveIndex
                }
            }
            when (i.shape) {
                in containers -> containers.remove(i.shape)
                else -> containers.add(i.shape)
            }
            if (i == this) {
                n2 = when {
                    containers.isEmpty() -> 1.0
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
