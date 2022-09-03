package uk.co.kievits.raytracer.world

import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.model.Sphere

data class World(
    val objects: MutableList<Sphere> = mutableListOf(),
    val light: PointLight? = null,
) {
    companion object {
        fun default() = World(
            objects = mutableListOf(
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
