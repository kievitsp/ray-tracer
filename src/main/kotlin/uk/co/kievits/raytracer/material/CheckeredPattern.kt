package uk.co.kievits.raytracer.material

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.POINT
import kotlin.math.floor

class CheckeredPattern(
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
        val check = floor(shapePoint.x) + floor(shapePoint.y) + floor(shapePoint.z)
        return if (floor(check) % 2.0f == .0f) first.atPattern(shapePoint) else second.atPattern(shapePoint)
    }
}
