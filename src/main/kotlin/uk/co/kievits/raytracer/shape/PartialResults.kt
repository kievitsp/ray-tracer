package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.VECTOR
import kotlin.math.pow
import kotlin.math.sqrt

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
) {
    fun schlick(): V {
        val cos = when {
            n1 <= n2 -> eyeV dot normalV
            else -> {
                val cos = eyeV dot normalV
                val n = n1 / n2
                val sin2t = n.pow(2) * (1f - cos.pow(2))
                if (sin2t > 1f) return 1f

                val cosT = sqrt(1f - sin2t)
                cosT
            }
        }

        val r0 = ((n1 - n2) / (n1 + n2)).pow(2)
        return r0 + (1 - r0) * (1 - cos).pow(5)
    }
}
