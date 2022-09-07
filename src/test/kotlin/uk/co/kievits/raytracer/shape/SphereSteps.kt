package uk.co.kievits.raytracer.shape

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.approx
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.cucumber.SharedVars.buildMatrix
import uk.co.kievits.raytracer.cucumber.SharedVars.numberPattern
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloat
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloats
import uk.co.kievits.raytracer.cucumber.SharedVars.vars
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.material.Pattern
import uk.co.kievits.raytracer.material.TestPattern
import java.lang.IllegalStateException

class SphereSteps : En {

    init {
        ParameterType(
            "shape",
            "([scpAB]\\w*|inner|outer|object|cyl)|(sphere|test_shape|plane|glass_sphere|cube|cylinder)\\(\\)"
        ) { value, new ->
            when {
                value != null -> SharedVars.get<Shape>(value)
                else -> when (new) {
                    "sphere" -> Sphere()
                    "glass_sphere" -> Sphere.Glass()
                    "plane" -> Plane()
                    "test_shape" -> TestShape()
                    "cube" -> Cube()
                    "cylinder" -> Cylinder()
                    else -> TODO(new.toString())
                }
            }
        }
        ParameterType("pattern", "pattern|test_pattern\\(\\)") { name -> getPattern(name) }

        Given("{variable} ← {shape}") { name: String, shape: Shape -> SharedVars[name] = shape }

        Given("{shape}.maximum ← {number}") { shape: Cylinder, value: V -> shape.maximum = value }
        Given("{shape}.minimum ← {number}") { shape: Cylinder, value: V -> shape.minimum = value }
        Given("{shape}.closed ← {boolean}") { shape: Cylinder, value: Boolean -> shape.closed = value }

        Given("{variable} ← normalize\\({tuple})") { name: String, tuple: TUPLE ->
            SharedVars[name] = tuple.normalise
        }

        Given("{} ← {shape} with:") { name: String, shape: Shape, data: DataTable ->
            applyShapeData(shape, data)
            SharedVars[name] = shape
        }

        Given("{shape} has:") { shape: Shape, data: DataTable ->
            applyShapeData(shape, data)
        }

        When("xs ← local_intersect\\({shape}, {ray})") { shape: Shape, ray: Ray ->
            vars["xs"] = shape.localIntersections(ray)
        }

        When("xs ← intersect\\({shape}, {ray})") { shape: Shape, ray: Ray ->
            vars["xs"] = shape.intersections(ray)
        }

        When("set_transform\\({shape}, {matrix})") { shape: Shape, matrix: Matrix<D4> ->
            shape.transform = matrix
        }

        Then("xs.count = {int}") { count: Int ->
            val xs = SharedVars.get<Intersections>("xs")
            assert(xs.size == count)
        }

        Then("xs is empty") {
            val xs = SharedVars.get<Intersections>("xs")
            assert(xs.isEmpty())
        }

        Then("{intersection}.t = {float}") { intersection: Intersection, exp: Float ->
            assert(intersection.t approx exp)
        }

        Then("{intersection}.object = {shape}") { intersection: Intersection, shape: Shape ->
            assert(intersection.shape == shape)
        }

        Then("{variable}.transform = {matrix}") { name: String, matrix: MATRIX ->
            val value: WorldAware = SharedVars[name]
            assert(value.transform == matrix)
        }

        Then("{shape}.material = {material}") { shape: Shape, material: Material ->
            assert(shape.material == material)
        }

        Then("{shape}.saved_ray.origin = {tuple}") { shape: TestShape, exp: TUPLE ->
            assert(shape.savedRay?.origin == exp)
        }

        Then("{shape}.saved_ray.direction = {tuple}") { shape: TestShape, exp: TUPLE ->
            assert(shape.savedRay?.direction == exp)
        }

        Then("{shape}.material.{variable} = {float}") { shape: Shape, variable: String, exp: Float ->
            assertMaterial(shape.material, variable, exp)
        }

        Then("{shape}.minimum = {number}") { shape: Cylinder, exp: Float ->
            assert(shape.minimum == exp)
        }
        Then("{shape}.maximum = {number}") { shape: Cylinder, exp: Float ->
            assert(shape.maximum == exp)
        }
        Then("{shape}.closed = {boolean}") { shape: Cylinder, exp: Boolean ->
            assert(shape.closed == exp)
        }
    }

    private fun getPattern(name: String) = when (name) {
        "test_pattern()" -> TestPattern()
        else -> SharedVars.get<Pattern>(name)
    }

    private fun applyShapeData(shape: Shape, data: DataTable) {
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
                    "material.reflective" -> material.reflective = parseFloat(value)
                    "material.specular" -> material.specular = parseFloat(value)
                    "material.refractive_index" -> material.refractiveIndex = parseFloat(value)
                    "material.transparency" -> material.transparency = parseFloat(value)
                    "material.ambient" -> material.ambient = parseFloat(value)
                    "material.pattern" -> material.pattern = getPattern(value)
                    "transform" -> {
                        val matcher = "(\\w+)\\($numberPattern\\)".toRegex()
                        val result = matcher.matchEntire(value) ?: throw IllegalStateException(value)
                        transform = buildMatrix(null, result.groupValues[1], result.groupValues[2]) as Matrix<D4>
                    }

                    else -> TODO(row[0])
                }
            }
        }
    }
}
