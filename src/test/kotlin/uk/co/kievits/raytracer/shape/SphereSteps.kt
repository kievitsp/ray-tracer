package uk.co.kievits.raytracer.shape

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.approx
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.cucumber.SharedVars.get
import uk.co.kievits.raytracer.cucumber.SharedVars.vars
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.model.Sphere

class SphereSteps : En {

    init {
        ParameterType(
            "sphere",
            "(s[a-z]*)|sphere\\(\\)"
        ) { value ->
            when (value) {
                null -> Sphere()
                else -> get<Sphere>(value)
            }
        }

        Given("{} ← {sphere}") { name: String, sphere: Sphere ->
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
