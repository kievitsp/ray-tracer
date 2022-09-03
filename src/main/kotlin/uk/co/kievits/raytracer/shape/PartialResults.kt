package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.model.Sphere

data class PartialResults(
    val t: V,
    val shape: Sphere,
    val point: POINT,
    val eyeV: VECTOR,
    val normalV: VECTOR,
    val isInside: Boolean,
) {
    fun shadeHit(light: PointLight): COLOR = shape.material.lighting(
        light = light,
        point = point,
        eyeV = eyeV,
        normalV = normalV,
        inShadow = false
    )
}
