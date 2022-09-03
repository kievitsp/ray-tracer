package uk.co.kievits.raytracer.shape

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.approx
import uk.co.kievits.raytracer.model.Intersections
import uk.co.kievits.raytracer.model.SharedVars.get
import uk.co.kievits.raytracer.model.SharedVars.vars
import uk.co.kievits.raytracer.model.Sphere

class SphereSteps : En {
    lateinit var s: Sphere

    init {
        Given("s ← sphere\\()") {
            s = Sphere()
        }

        When("xs ← intersect\\(s, r)") {
            vars["xs"] = s.intersections(get("r"))
        }

        When("set_transform\\(s, {mVar})") { matrix: Matrix<D4> ->
            s.transform = matrix
        }

        Then("xs.count = {int}") { count: Int ->
            val xs = get<Intersections>("xs")
            assert(xs.size == count)
        }

        Then("xs[0].t = {float}") { exp: Float ->
            val xs = get<Intersections.Hits>("xs")
            assert(xs[0].t approx exp)
        }

        Then("xs[1].t = {float}") { exp: Float ->
            val xs = get<Intersections.Hits>("xs")
            assert(xs[1].t approx exp)
        }

        Then("xs[0].object = s") {
            val xs = get<Intersections.Hits>("xs")
            assert(xs[0].shape == s)
        }

        Then("xs[1].object = s") {
            val xs = get<Intersections.Hits>("xs")
            assert(xs[1].shape == s)
        }

        Then("s.transform = {}") { name: String ->
            val matrix = when (name) {
                "identity_matrix" -> IdentityMatrix()
                else -> get(name)
            }
            assert(s.transform == matrix)
        }
    }
}
