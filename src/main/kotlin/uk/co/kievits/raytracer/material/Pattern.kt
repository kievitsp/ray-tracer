package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.shape.Shape
import uk.co.kievits.raytracer.shape.WorldAware

abstract class Pattern(
    transform: Matrix<D4>,
) : WorldAware(transform) {

    constructor() : this(IdentityMatrix())

    abstract fun at(point: POINT): COLOR
    open fun atShape(shape: Shape, point: POINT): COLOR {
        val shapePoint = shape.inverseTranspose * point
        val patternPoint = inverseTransform * shapePoint

        return at(patternPoint)
    }
}
