package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.model.Sphere

data class Intersection(
    val t: V,
    val shape: Sphere,
)
