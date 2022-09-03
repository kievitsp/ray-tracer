package uk.co.kievits.raytracer.cucumber

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.rotationX
import uk.co.kievits.raytracer.base.rotationY
import uk.co.kievits.raytracer.base.rotationZ
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.base.shearing
import uk.co.kievits.raytracer.base.translation
import uk.co.kievits.raytracer.model.SharedVars
import uk.co.kievits.raytracer.model.SharedVars.buildMatrix
import kotlin.math.sqrt

class SharedSteps : En {
    init {
        ParameterType(
            "stringVar", "[a-z]\\w*"
        ) { value ->
            SharedVars.get<String>(value)
        }

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



        Given("{} ← {tuple}") { name: String, tuple: TUPLE ->
            SharedVars.vars[name] = tuple
        }

        Given("{} ← {mVar}") { name: String, matrix: MATRIX ->
            SharedVars.vars[name] = matrix
        }

    }
}
