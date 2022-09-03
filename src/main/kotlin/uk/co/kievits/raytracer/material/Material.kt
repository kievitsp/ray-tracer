package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.approx

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

    companion object {
        private val defaultColor: COLOR = Color(1, 1, 1)
        private const val defaultAmbient: V = 0.1f
        private const val defaultDiffuse: V = 0.9f
        private const val defaultSpecular: V = 0.9f
        private const val defaultShininess: V = 200f
    }
}
