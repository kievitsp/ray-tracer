package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.shape.Shape
import uk.co.kievits.raytracer.shape.WorldAware

abstract class Pattern(
    transform: Matrix<D4>,
) : WorldAware(transform) {
    abstract fun at(point: POINT): COLOR
    abstract fun atShape(shape: Shape, point: POINT): COLOR
}
