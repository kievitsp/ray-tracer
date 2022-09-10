package uk.co.kievits.raytracer.world

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Colors.BLACK
import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.shape.Intersections
import uk.co.kievits.raytracer.shape.PartialResults
import uk.co.kievits.raytracer.shape.Shape
import uk.co.kievits.raytracer.shape.Sphere
import kotlin.math.pow
import kotlin.math.sqrt

data class World(
    val shapes: MutableList<Shape> = mutableListOf(),
    var lights: MutableList<PointLight>,
) {
    constructor(
        shapes: MutableList<Shape> = mutableListOf(),
        light: PointLight? = null,
    ) : this(shapes, listOfNotNull(light).toMutableList())

    var light: PointLight?
        get() = lights.firstOrNull()
        set(value) {
            lights.clear()
            if (value != null) lights.add(value)
        }

    fun intersections(ray: Ray): Intersections = Intersections(
        hits = shapes
            .asSequence()
            .flatMap { it.intersections(ray) }
            .toMutableList(),
        isSorted = false
    )

    fun shadeHit(
        comps: PartialResults,
        remaining: Int = Int.MAX_VALUE,
    ): COLOR {
        val reflected = reflectedColor(comps, remaining)
        val refracted = refractedColor(comps, remaining)
        val initial = if (comps.shape.material.reflective > 0 && comps.shape.material.transparency > 0) {
            val reflectance = comps.schlick()
            (reflected * reflectance) + (refracted * (1 - reflectance))
        } else {
            reflected + refracted
        }
        return lights.fold(initial) { acc, light -> acc + lighting(comps, light) }
    }

    private fun lighting(
        comps: PartialResults,
        light: PointLight
    ) = comps.shape.material.lighting(
        light = light,
        point = comps.overPoint,
        eyeV = comps.eyeV,
        normalV = comps.normalV,
        isShadowed = isShadowed(comps.overPoint, light),
        shape = comps.shape,
    )

    fun reflectedColor(
        comps: PartialResults,
        remaining: Int = Int.MAX_VALUE,
    ): COLOR {
        if (remaining <= 0) return BLACK
        val reflective = comps.shape.material.reflective
        if (reflective < EPSILON) return BLACK

        val reflectRay = Ray(comps.overPoint, comps.reflectV)
        return colorAt(reflectRay, remaining - 1) * reflective
    }

    fun refractedColor(
        comps: PartialResults,
        remaining: Int = Int.MAX_VALUE,
    ): COLOR {
        val transparency = comps.shape.material.transparency
        if (transparency == 0.0 || remaining <= 0) return BLACK

        val nRatio = comps.n1 / comps.n2
        val cosI = comps.eyeV dot comps.normalV
        val sin2t = nRatio.pow(2) * (1 - cosI.pow(2))

        if (sin2t > 1) return BLACK

        val cosT = sqrt(1 - sin2t)
        val direction = comps.normalV * (nRatio * cosI - cosT) - comps.eyeV * nRatio
        val refractedRay = Ray(comps.underPoint, direction)

        return colorAt(refractedRay, remaining - 1) * transparency
    }

    fun colorAt(
        ray: Ray,
        remaining: Int = 10,
    ): COLOR {
        val intersections = intersections(ray)
        val intersection = intersections.hit() ?: return BLACK

        val precompute = intersection.precompute(ray, intersections)

        return shadeHit(precompute, remaining)
    }

    fun isShadowed(
        point: POINT,
        light: PointLight = this.light!!
    ): Boolean {
        val v = light.position - point
        val distance = v.magnitude
        val direction = v.normalise

        val r = Ray(point, direction)
        val hit = intersections(r).hit()

        return hit != null && hit.t < distance
    }

    companion object {
        fun default() = World(
            shapes = mutableListOf(
                Sphere().apply {
                    material.color = Color(0.8, 1.0, 0.6)
                    material.diffuse = 0.7
                    material.specular = 0.2
                },
                Sphere().apply {
                    transform = scaling(0.5, 0.5, 0.5)
                }
            ),
            light = PointLight(
                position = Point(-10, 10, -10),
                intensity = Color(1, 1, 1)
            )
        )
    }
}
