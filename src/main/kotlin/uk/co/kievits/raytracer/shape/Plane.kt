package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.Tuple
import uk.co.kievits.raytracer.material.Material

class Plane(
    transform: Matrix<D4> = IdentityMatrix(),
    material: Material = Material(),
) : Shape(transform, material) {
    override fun localNormalAt(p: POINT): Tuple {
        TODO("Not yet implemented")
    }

    override fun localIntersections(ray: Ray): Intersections {
        TODO("Not yet implemented")
    }
}
