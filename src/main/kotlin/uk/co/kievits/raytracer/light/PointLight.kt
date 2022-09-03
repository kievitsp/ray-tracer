package uk.co.kievits.raytracer.light

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT

data class PointLight(
    val position: POINT,
    val intensity: COLOR,
)
