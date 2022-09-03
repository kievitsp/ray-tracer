package uk.co.kievits.raytracer.cucumber

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.cucumber.SharedVars.numberPattern
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloat
import uk.co.kievits.raytracer.world.World

class SharedSteps : En {
    init {
        ParameterType(
            "stringVar", "[a-z]\\w*"
        ) { value ->
            SharedVars.get<String>(value)
        }

        ParameterType(
            "variable",
            "[a-zA-Z]\\w*"
        ) { value -> value }

        ParameterType(
            "number", numberPattern
        ) { value: String ->
            parseFloat(value)
        }

        ParameterType(
            "operator", "\\+|-|\\*|/"
        ) { value ->
            when (value) {
                "+" -> Operator.PLUS
                "-" -> Operator.MINUS
                "*" -> Operator.TIMES
                "/" -> Operator.DIV
                else -> TODO(value)
            }
        }

        ParameterType("boolean", "true|false") { value -> value.toBoolean() }

        ParameterType(
            "world",
            "w|world\\(\\)|default_world\\(\\)",
        ) { name ->
            when (name) {
                "world()" -> World()
                "default_world()" -> World.default()
                else -> SharedVars.get<World>(name)
            }
        }

        Given("{variable} ← {tuple}") { name: String, tuple: TUPLE ->
            SharedVars[name] = tuple
        }

        Given("{variable} ← {mVar}") { name: String, matrix: MATRIX ->
            SharedVars[name] = matrix
        }
        Given("{variable} ← {world}") { name: String, world: World ->
            SharedVars[name] = world
        }

        Then("{} is nothing") { name: String ->
            assert(name !in SharedVars.vars)
        }

        Then("{tuple} = {tuple}") { actual: TUPLE, exp: TUPLE ->
            assert(actual approx exp)
        }

        Given("{variable} ← {mVar} * {mVar}") { name: String, m1: Matrix<D4>, m2: Matrix<D4> ->
            SharedVars.vars[name] = m1 * m2
        }
    }
}
