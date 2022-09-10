package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.toV
import uk.co.kievits.raytracer.shape.Shape
import uk.co.kievits.raytracer.shape.WorldAware

abstract class Pattern(
    transform: Matrix<D4>,
) : WorldAware(transform) {

    constructor() : this(IdentityMatrix())

    abstract fun atPattern(shapePoint: POINT): COLOR

    open fun atShape(shape: Shape, point: POINT): COLOR {
        val shapePoint = shape.transform.inverse.transpose * point
        // todo fix patterns..
//        val patternPoint = transform.inverse.transpose * shapePoint
        return atPattern(shapePoint)
    }

    fun perturbed(ratio: Number = 1f) = PerturbedPattern(this, ratio.toFloat())

    operator fun plus(other: Pattern): Pattern = CombinedPattern(this, other)
    operator fun div(scalar: Number): Pattern = DivisionPattern(this, scalar.toV())
}
