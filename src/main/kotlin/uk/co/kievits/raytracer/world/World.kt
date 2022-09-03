package uk.co.kievits.raytracer.world

import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.model.Sphere

data class World(
    val objects: MutableList<Sphere> = mutableListOf(),
    val lights: MutableList<PointLight> = mutableListOf(),
)
