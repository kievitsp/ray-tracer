package uk.co.kievits.raytracer.shape

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.approx
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.cucumber.SharedVars.buildMatrix
import uk.co.kievits.raytracer.cucumber.SharedVars.numberPattern
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloat
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloats
import uk.co.kievits.raytracer.cucumber.SharedVars.vars
import uk.co.kievits.raytracer.material.Material
import java.lang.IllegalStateException

class SphereSteps : En {

    init {
        ParameterType(
            "shape",
            "([sp]\\w*|inner|outer)|(sphere|test_shape|plane)\\(\\)"
        ) { value, new ->
            when {
                value != null -> SharedVars.get<Shape>(value)
                else -> when (new) {
                    "sphere" -> Sphere()
                    "plane" -> Plane()
                    "test_shape" -> TestShape()
                    else -> TODO(new.toString())
                }
            }
        }

        Given("{variable} ← {shape}") { name: String, shape: Shape ->
            SharedVars[name] = shape
        }

        Given("{} ← {shape} with:") { name: String, shape: Shape, data: DataTable ->
            shape.apply {
                for (r in 0 until data.height()) {
                    val row = data.row(r)
                    val value = row[1]
                    when (row[0]) {
                        "material.color" -> {
                            val floats = parseFloats(value.substring(1 until value.length - 1))
                            material.color = Color(floats[0], floats[1], floats[2])
                        }

                        "material.diffuse" -> material.diffuse = parseFloat(value)
                        "material.specular" -> material.specular = parseFloat(value)
                        "transform" -> {
                            val matcher = "(\\w+)\\($numberPattern\\)".toRegex()
                            val result = matcher.matchEntire(value) ?: throw IllegalStateException(value)
                            transform = buildMatrix(null, result.groupValues[1], result.groupValues[2]) as Matrix<D4>
                        }
                    }
                }
            }
            SharedVars[name] = shape
        }

        When("xs ← local_intersect\\({shape}, {ray})") { shape: Shape, ray: Ray ->
            vars["xs"] = shape.intersections(ray)
        }
        When("xs ← intersect\\({shape}, {ray})") { shape: Shape, ray: Ray ->
            vars["xs"] = shape.localIntersections(ray)
        }

        When("set_transform\\({shape}, {mVar})") { shape: Shape, matrix: Matrix<D4> ->
            shape.transform = matrix
        }

        Then("xs.count = {int}") { count: Int ->
            val xs = SharedVars.get<Intersections>("xs")
            assert(xs.size == count)
        }

        Then("{intersection}.t = {float}") { intersection: Intersection, exp: Float ->
            assert(intersection.t approx exp)
        }

        Then("{intersection}.object = {shape}") { intersection: Intersection, shape: Shape ->
            assert(intersection.shape == shape)
        }

        Then("{shape}.transform = {mVar}") { shape: Shape, matrix: MATRIX ->
            assert(shape.transform == matrix)
        }

        Then("{shape}.material = {material}") { shape: Shape, material: Material ->
            assert(shape.material == material)
        }

        Then("{shape}.saved_ray.origin = {tuple}") { shape: TestShape, exp: TUPLE ->
            assert(shape.savedRay.origin == exp)
        }

        Then("{shape}.saved_ray.direction = {tuple}") { shape: TestShape, exp: TUPLE ->
            assert(shape.savedRay.direction == exp)
        }
    }
}
