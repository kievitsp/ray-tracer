package uk.co.kievits.raytracer.shape

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.model.SharedVars

class ShapeSteps : En {
    init {
        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\((.*?)\\)|(zero|norm|origin|direction)",
        ) { type: String?, args: String?, name: String? ->
            SharedVars.buildTuple(args, type, name)
        }

        ParameterType(
            "mVar",
            "(m\\w*|IDENTITY_MATRIX|identity_matrix|)|" +
                    "(translation|scaling|shearing|rotation_[xyz])\\((.*?)\\)"
        ) { name, function, args ->
            SharedVars.buildMatrix(name, function, args)
        }

        ParameterType(
            "ray",
            "r\\d?",
        ) { name ->
            SharedVars.get<Ray>(name)
        }

        Given("{} ← ray\\({tuple}, {tuple})") { name: String, origin: TUPLE, direction: TUPLE ->
            SharedVars.vars[name] = Ray(origin, direction)
        }

        Then("{ray}.origin = {tuple}") { ray: Ray, exp: TUPLE ->
            assert(ray.origin == exp)
        }

        Then("{ray}.direction = {tuple}") { ray: Ray, exp: TUPLE ->
            assert(ray.direction == exp)
        }

        Then("position\\({ray}, {float}) = {tuple}") { ray: Ray, point: Float, exp: TUPLE ->
            assert(ray.position(point) == exp)
        }

        When("{} ← transform\\({ray}, {mVar})") { name: String, ray: Ray, m: MATRIX ->
            SharedVars.vars[name] = ray.transform(m)
        }

    }
}
