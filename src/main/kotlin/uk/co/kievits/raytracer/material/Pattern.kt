package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.shape.Shape

interface Pattern {
    fun at(point: POINT): COLOR
    fun atShape(shape: Shape, point: POINT): COLOR
}
