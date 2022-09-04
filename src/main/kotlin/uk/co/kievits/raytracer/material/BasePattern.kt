package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT

abstract class BasePattern : Pattern() {
    abstract fun at(point: POINT): COLOR
    override fun atPattern(shapePoint: POINT): COLOR = at(inverseTransform * shapePoint)
}
