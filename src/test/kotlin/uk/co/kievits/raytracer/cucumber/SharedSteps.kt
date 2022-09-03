package uk.co.kievits.raytracer.cucumber

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.TUPLE
import kotlin.math.sqrt

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
            "number", "(√)?(-?\\d+(\\.\\d+)?)|(-?\\d+/-?\\d+)"
        ) { sqrt: String?, value: String?, division: String? ->
            if (value != null) {
                val float = value.toFloat()
                if (sqrt == null) float else sqrt(float)
            } else if (division != null) {
                val floats = division.split('/').map { it.toFloat() }
                floats[0] / floats[1]
            } else {
                TODO()
            }
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

        Given("{} ← {tuple}") { name: String, tuple: TUPLE ->
            SharedVars[name] = tuple
        }

        Given("{variable} ← {mVar}") { name: String, matrix: MATRIX ->
            SharedVars[name] = matrix
        }

        Then("{} is nothing") { name: String ->
            assert(name !in SharedVars.vars)
        }

        Then("{tuple} = {tuple}") { actual: TUPLE, exp: TUPLE ->
            assert(actual == exp)
        }

        Given("{} ← {mVar} * {mVar}") { name: String, m1: MATRIX, m2: MATRIX ->
            SharedVars.vars[name] = m1 * m2
        }
    }
}
