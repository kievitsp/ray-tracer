package uk.co.kievits.raytracer.world

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Colors
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.model.Sphere
import uk.co.kievits.raytracer.shape.Intersections
import uk.co.kievits.raytracer.shape.PartialResults

data class World(
    val shapes: MutableList<Sphere> = mutableListOf(),
    var light: PointLight? = null,
) {

    fun intersections(ray: Ray): Intersections = Intersections(
        hits = shapes.asSequence()
            .flatMap { it.intersections(ray) }
            .sortedBy { it.t }
            .toList(),
        isSorted = true
    )

    fun shadeHit(comps: PartialResults): COLOR {
        return comps.shadeHit(light ?: return Colors.BLACK)
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
