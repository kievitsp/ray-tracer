package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.VECTOR

data class PartialResults(
    val t: V,
    val shape: Shape,
    val point: POINT,
    val eyeV: VECTOR,
    val normalV: VECTOR,
    val isInside: Boolean,
    val overPoint: POINT,
    val underPoint: POINT,
    val reflectV: VECTOR,
    val n1: V,
    val n2: V,
)
