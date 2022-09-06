package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Vector
import uk.co.kievits.raytracer.noise.ImprovedNoise

class PerturbedPattern(
    private val base: Pattern,
    private val ratio: Float,
) : Pattern() {

    override fun atPattern(shapePoint: POINT): COLOR {
        val x = shapePoint.x
        val y = shapePoint.y
        val z = shapePoint.z
        val noiseX = ImprovedNoise.noise(x, y, z) * ratio
        val noiseY = ImprovedNoise.noise(y, z, x) * ratio
        val noiseZ = ImprovedNoise.noise(z, x, y) * ratio
        val noise = Vector(noiseX, noiseY, noiseZ)
        return base.atPattern(shapePoint * noise)
    }
}
