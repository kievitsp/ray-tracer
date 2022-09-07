package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT

class CombinedPattern(
    private val first: Pattern,
    private val second: Pattern,
) : Pattern() {
    override fun atPattern(shapePoint: POINT): COLOR {
        val first = first.atPattern(first.transform.inverse * shapePoint)
        val second = second.atPattern(second.transform.inverse * shapePoint)

        return first + second
    }
}
