package uk.co.kievits.raytracer.world

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Colors
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.shape.Intersections
import uk.co.kievits.raytracer.shape.PartialResults
import uk.co.kievits.raytracer.shape.Shape
import uk.co.kievits.raytracer.shape.Sphere

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
        hits = shapes.asSequence()
            .flatMap { it.intersections(ray) }
            .sortedBy { it.t }
            .toList(),
        isSorted = true
    )

    fun shadeHit(comps: PartialResults): COLOR = lights.fold(Colors.BLACK) { acc, light ->
        acc + lighting(comps, light)
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

    fun colorAt(ray: Ray): COLOR {
        val intersection = intersections(ray)
            .hit() ?: return Colors.BLACK

        val precompute = intersection.precompute(ray)

        return shadeHit(precompute)
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
                    material.diffuse = 0.7f
                    material.specular = 0.2f
                },
                Sphere().apply {
                    transform = scaling(0.5f, 0.5f, 0.5f)
                }
            ),
            light = PointLight(
                position = Point(-10, 10, -10),
                intensity = Color(1, 1, 1)
            )
        )
    }
}
