package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

class RingPattern(
    private val first: Pattern,
    private val second: Pattern,
) : Pattern() {
    constructor(
        first: COLOR,
        second: COLOR,
    ) : this(
        SolidPattern(first),
        SolidPattern(second),
    )

    override fun atPattern(shapePoint: POINT): COLOR {
        val distance = sqrt(shapePoint.x.pow(2) + shapePoint.z.pow(2))
        return if (floor(distance) % 2.0 == .0) first.atPattern(shapePoint) else second.atPattern(shapePoint)
    }
}
