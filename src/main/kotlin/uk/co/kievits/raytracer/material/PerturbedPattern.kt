package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.noise.ImprovedNoise

class PerturbedPattern(
    private val base: Pattern,
    private val ratio: Float,
) : Pattern() {

    override fun atPattern(shapePoint: POINT): COLOR {
        val noise = ImprovedNoise.noise(shapePoint.x, shapePoint.y, shapePoint.z) * ratio
        return base.atPattern(
            Point(
                shapePoint.x + noise,
                shapePoint.y + noise,
                shapePoint.z + noise
            )
        )
    }
}
