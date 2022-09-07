package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.Tuple
import uk.co.kievits.raytracer.material.Material

class TestShape(
    transform: Matrix<D4> = IdentityMatrix(),
    material: Material = Material(),
) : Shape(transform, material) {
    var savedPoint: POINT? = null
    var savedRay: Ray? = null

    override fun localNormalAt(p: POINT): Tuple {
        savedPoint = p
        return p
    }

    override fun localIntersections(ray: Ray): Intersections {
        savedRay = ray
        return Intersections.Miss
    }
}
