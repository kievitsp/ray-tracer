package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.base.approx
import uk.co.kievits.raytracer.light.PointLight
import kotlin.math.pow

data class Material(
    var color: COLOR = defaultColor,
    var ambient: V = defaultAmbient,
    var diffuse: V = defaultDiffuse,
    var specular: V = defaultSpecular,
    var shininess: V = defaultShininess,
) {
    override fun equals(other: Any?): Boolean {
        if (other !is Material) return false

        return color approx other.color &&
            ambient approx other.ambient &&
            diffuse approx other.diffuse &&
            specular approx other.specular &&
            shininess approx other.shininess
    }

    fun lighting(
        light: PointLight,
        point: POINT,
        eyeV: VECTOR,
        normalV: VECTOR,
        inShadow: Boolean,
    ): COLOR {
        val effectiveColor = color * light.intensity
        val lightV = (light.position - point).normalise

        val ambient = effectiveColor * this.ambient

        val lightDotNormal = lightV dot normalV

        return when {
            lightDotNormal < 0 -> ambient
            else -> {
                val diffuse: COLOR = effectiveColor * this.diffuse * lightDotNormal
                val reflectV = (-lightV) reflect normalV
                val reflectDotEye = reflectV dot eyeV

                when {
                    reflectDotEye <= 0 -> ambient + diffuse
                    else -> {
                        val factor = reflectDotEye.pow(this.shininess)
                        val specular = light.intensity * this.specular * factor
                        ambient + diffuse + specular
                    }
                }
            }
        }
    }

    override fun toString(): String =
        "Material(color=$color, ambient=$ambient, diffuse=$diffuse, specular=$specular, shininess=$shininess)"

    companion object {
        private val defaultColor: COLOR = Color(1, 1, 1)
        private const val defaultAmbient: V = 0.1f
        private const val defaultDiffuse: V = 0.9f
        private const val defaultSpecular: V = 0.9f
        private const val defaultShininess: V = 200f
    }
}
