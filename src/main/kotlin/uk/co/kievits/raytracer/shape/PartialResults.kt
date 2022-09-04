package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.light.PointLight

data class PartialResults(
    val t: V,
    val shape: Shape,
    val point: POINT,
    val eyeV: VECTOR,
    val normalV: VECTOR,
    val isInside: Boolean,
    val overPoint: POINT,
) {
    fun lighting(
        light: PointLight,
        isShadowed: Boolean,
    ) = shape.material.lighting(
        light = light,
        point = overPoint,
        eyeV = eyeV,
        normalV = normalV,
        isShadowed = isShadowed,
    )
}
