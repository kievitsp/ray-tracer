package uk.co.kievits.raytracer.shape

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.approx
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.cucumber.SharedVars.buildMatrix
import uk.co.kievits.raytracer.cucumber.SharedVars.get
import uk.co.kievits.raytracer.cucumber.SharedVars.numberPattern
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloat
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloats
import uk.co.kievits.raytracer.cucumber.SharedVars.vars
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.model.Sphere
import java.lang.IllegalStateException

class SphereSteps : En {

    init {
        ParameterType(
            "sphere",
            "(s\\w*|inner|outer)|sphere\\(\\)"
        ) { value ->
            when (value) {
                null -> Sphere()
                else -> get<Sphere>(value)
            }
        }

        Given("{} ← {sphere}") { name: String, sphere: Sphere ->
            SharedVars[name] = sphere
        }

        Given("{} ← {sphere} with:") { name: String, sphere: Sphere, data: DataTable ->
            sphere.apply {
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
            SharedVars[name] = sphere
        }

        When("xs ← intersect\\({sphere}, {ray})") { sphere: Sphere, ray: Ray ->
            vars["xs"] = sphere.intersections(ray)
        }

        When("set_transform\\({sphere}, {mVar})") { sphere: Sphere, matrix: Matrix<D4> ->
            sphere.transform = matrix
        }

        Then("xs.count = {int}") { count: Int ->
            val xs = get<Intersections>("xs")
            assert(xs.size == count)
        }

        Then("{intersection}.t = {float}") { intersection: Intersection, exp: Float ->
            assert(intersection.t approx exp)
        }

        Then("{intersection}.object = {sphere}") { intersection: Intersection, sphere: Sphere ->
            assert(intersection.shape == sphere)
        }

        Then("{sphere}.transform = {mVar}") { sphere: Sphere, matrix: MATRIX ->
            assert(sphere.transform == matrix)
        }
        Then("{sphere}.material = {material}") { sphere: Sphere, material: Material ->
            assert(sphere.material == material)
        }
    }
}
