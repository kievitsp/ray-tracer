package uk.co.kievits.raytracer.model

import io.cucumber.java8.En
import uk.co.kievits.raytracer.model.SharedVars.getVar
import uk.co.kievits.raytracer.model.SharedVars.vars


class SphereSteps : En {
    lateinit var s: Sphere

    init {
        Given("s ← sphere\\()") {
            s = Sphere()
        }

        When("xs ← intersect\\(s, r)") {
            vars["xs"] = s.intersections(getVar("r"))
        }

        Then("xs.count = {int}") { count: Int ->
            val xs = getVar<Intersections>("xs")
            assert(xs.hits == count)
        }

        Then("xs[0] = {float}") { exp: Float ->
            val xs = getVar<Intersections.Hit>("xs")
            assert(xs.t1 approx exp)
        }

        Then("xs[1] = {float}") { exp: Float ->
            val xs = getVar<Intersections.Hit>("xs")
            assert(xs.t2 approx exp)
        }

    }
}