package uk.co.kievits.raytracer.model

import io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.cucumber.java8.PendingException
import io.cucumber.junit.platform.engine.Cucumber
import org.junit.platform.suite.api.ConfigurationParameter
import uk.co.kievits.raytracer.model.SharedVars.getVar
import uk.co.kievits.raytracer.model.SharedVars.numberSplitter
import uk.co.kievits.raytracer.model.SharedVars.parseFloat
import uk.co.kievits.raytracer.model.SharedVars.parseFloats
import uk.co.kievits.raytracer.model.SharedVars.vars
import kotlin.collections.contentToString
import kotlin.collections.joinToString
import kotlin.collections.map
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.math.PI
import kotlin.math.sqrt

@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME, value = "uk.co.kievits.shared"
)
class StepDefs : En {


    enum class Operator {
        PLUS, MINUS, TIMES, DIV,
    }


    init {


        DataTableType { dataTable: DataTable ->
            val height = dataTable.height()
            val width = dataTable.width()

            val array = FloatArray(height * width) { id ->
                val row = id / width
                val column = id % width
                dataTable.row(row)[column].toFloat()
            }

//            val rows: Array<FloatArray> = (0 until height).map { row ->
//                dataTable.row(row).map { cells -> cells.toFloat() }.toFloatArray()
//            }.toTypedArray()
//            mk.ndarray(rows)
            when (array.size) {
                16 -> Matrix.D4(array)
                9 -> Matrix.D3(array)
                4 -> Matrix.D2(array)
                else -> TODO(array.contentToString())
            }

        }

        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\((.*?)\\)|([a-z]\\w?|zero|norm|origin|direction)",
        ) { type: String?, args: String?, name: String? ->
            if (args != null && type != null) {
                val floats = parseFloats(args)
                when (type) {
                    "tuple" -> Tuple(floats[0], floats[1], floats[2], floats[3])
                    "point" -> Point(floats[0], floats[1], floats[2])
                    "vector" -> Vector(floats[0], floats[1], floats[2])
                    "color" -> Color(floats[0], floats[1], floats[2])
                    else -> TODO(type)
                }
            } else if (name != null) {
                getVar<TUPLE>(name)
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

        ParameterType(
            "stringVar", "[a-z]\\w*"
        ) { value ->
            getVar<String>(value)
        }

        ParameterType(
            "mVar", "([A-Z]\\w*|IDENTITY_MATRIX|\\w{3,})|" + "(translation|scaling|shearing|rotation_[xyz])\\((.*?)\\)"
        ) { name, function, args ->
            when {
                name != null -> {
                    when (name) {
                        "IDENTITY_MATRIX" -> IdentityMatrix()
                        else -> getVar<MATRIX>(name)
                    }
                }

                function != null -> {
                    val floats = args.split(numberSplitter).map { parseFloat(it) }
                    when (function) {
                        "translation" -> translation(floats[0], floats[1], floats[2])
                        "scaling" -> scaling(floats[0], floats[1], floats[2])
                        "shearing" -> shearing(floats[0], floats[1], floats[2], floats[3], floats[4], floats[5])
                        "rotation_x" -> rotationX(floats[0])
                        "rotation_y" -> rotationY(floats[0])
                        "rotation_z" -> rotationZ(floats[0])

                        else -> TODO(function)
                    }
                }

                else -> TODO()
            }
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

        Given("{} ← {tuple}") { name: String, tuple: TUPLE ->
            vars[name] = tuple
        }

        Given("{} ← ray\\({tuple}, {tuple})") { name: String, origin: TUPLE, direction: TUPLE ->
            vars[name] = Ray(origin, direction)
        }

        When("r2 ← transform\\({}, {})") { ray: String, m: String ->
            val ray: Ray = getVar(ray)
            val matrix: MATRIX = getVar(m)
            vars["r2"] = ray.transform(matrix)
        }

        Given("{} ← {mVar}") { name: String, matrix: MATRIX ->
            vars[name] = matrix
        }

        Then("{} ← submatrix\\({mVar}, {int}, {int})") { name: String, m: MATRIX, x: Int, y: Int ->
            vars[name] = m.subMatrix(x, y)
        }

        Given("the following 2x2 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("the following 4x4 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("the following 3x3 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }

        Given("{} ← {mVar} * {mVar}") { name: String, m1: MATRIX, m2: MATRIX ->
            vars[name] = m1 * m2
        }

        Given("the following matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("{} ← transpose\\({mVar})") { name: String, m: MATRIX -> vars[name] = m.transpose() }
        Given("{} ← inverse\\({mVar})") { name: String, m: MATRIX -> vars[name] = m.inverse() }

        Then("{tuple}.x = {float}") { tuple: TUPLE, value: Float -> assert(tuple.x == value) }
        Then("{tuple}.y = {float}") { tuple: TUPLE, value: Float -> assert(tuple.y == value) }
        Then("{tuple}.z = {float}") { tuple: TUPLE, value: Float -> assert(tuple.z == value) }
        Then("{tuple}.w = {float}") { tuple: TUPLE, value: Float -> assert(tuple.w == value) }

        Then("{tuple}.red = {float}") { tuple: TUPLE, value: Float -> assert(tuple.red == value) }
        Then("{tuple}.green = {float}") { tuple: TUPLE, value: Float -> assert(tuple.green == value) }
        Then("{tuple}.blue = {float}") { tuple: TUPLE, value: Float -> assert(tuple.blue == value) }


        Then("{tuple} is a point") { tuple: TUPLE ->
            assert(tuple.isPoint)
        }
        Then("{tuple} is not a point") { tuple: TUPLE ->
            assert(!tuple.isPoint)
        }

        Then("{tuple} is a vector") { tuple: TUPLE ->
            assert(tuple.isVector)
        }
        Then("{tuple} is not a vector") { tuple: TUPLE ->
            assert(!tuple.isVector)
        }

        Then("{tuple} = {tuple}") { value: TUPLE, tuple: TUPLE ->
            assert(value == tuple)
        }

        Then("-{tuple} = {tuple}") { value: TUPLE, tuple: TUPLE ->
            assert(-value == tuple)
        }

        Then("{tuple} {operator} {tuple} = {tuple}") { a: TUPLE, op: Operator, b: TUPLE, c: TUPLE ->
            assert(
                when (op) {
                    Operator.PLUS -> a + b
                    Operator.MINUS -> a - b
                    Operator.TIMES -> a * b
                    Operator.DIV -> a / b
                } approx c
            )
        }

        Then("{tuple} {operator} {float} = {tuple}") { a: TUPLE, op: Operator, b: Float, c: TUPLE ->
            assert(
                when (op) {
                    Operator.PLUS -> a + b
                    Operator.MINUS -> a - b
                    Operator.TIMES -> a * b
                    Operator.DIV -> a / b
                } == c
            )
        }

        Then("magnitude\\({tuple}) = {number}") { value: TUPLE, exp: Float -> assert(value.magnitude approx exp) }
        Then("normalize\\({tuple}) = {tuple}") { value: TUPLE, exp: TUPLE -> assert(value.normalise == exp) }
        Then("normalize\\({tuple}) = approximately {tuple}") { value: TUPLE, exp: TUPLE -> assert(value.normalise approx exp) }
        When("{} ← normalize\\({tuple})") { name: String, exp: TUPLE -> vars[name] = exp.normalise }
        Then("dot\\({tuple}, {tuple}) = {float}") { a: TUPLE, b: TUPLE, exp: Float -> assert(a dot b == exp) }
        Then("cross\\({tuple}, {tuple}) = {tuple}") { a: TUPLE, b: TUPLE, exp: TUPLE -> assert(a cross b == exp) }

        Then("lines {int}-{int} of {stringVar} are") { start: Int, end: Int, ppm: String, expected: String ->
            assert(ppm.split("\n").subList(start - 1, end).joinToString("\n") == expected)
        }


        Then("{stringVar} ends with a newline character") { ppm: String ->
            assert(ppm.lastOrNull() == '\n')
        }

        Then("{mVar}[{int},{int}] = {number}") { matrix: MATRIX, x: Int, y: Int, number: Float ->
            assert(matrix[x, y] approx number)
        }

        Then("{mVar} = {mVar}") { a: MATRIX, b: MATRIX -> assert(a approx b) }
        Then("{mVar} != {mVar}") { a: MATRIX, b: MATRIX -> assert(a napprox b) }

        Then("{mVar} * {mVar} is the following 4x4 matrix:") { a: MATRIX, b: MATRIX, c: MATRIX ->
            assert(a * b == c)
        }

        Then("{mVar} * {mVar} = {mVar}") { a: MATRIX, b: MATRIX, c: MATRIX ->
            assert(a * b == c)
        }

        Then("{mVar} * {tuple} = {tuple}") { a: MATRIX, b: TUPLE, c: TUPLE ->
            assert(a * b == c)
        }

        When("{} ← {mVar} * {tuple}") { name: String, a: MATRIX, b: TUPLE ->
            vars[name] = a * b
        }

        When("{} ← {mVar} * {mVar} * {mVar}") { name: String, a: MATRIX, b: MATRIX, c: MATRIX ->
            vars[name] = a * b * c
        }

        Then("transpose\\({mVar}) is the following matrix:") { a: MATRIX, b: MATRIX ->
            assert(a.transpose() == b)
        }

        Then("determinant\\({mVar}) = {float}") { a: MATRIX, value: Float ->
            assert(a.determinant() == value)
        }

        Then("submatrix\\({mVar}, {int}, {int}) is the following 3x3 matrix:") { m: MATRIX, x: Int, y: Int, exp: MATRIX ->
            assert(m.subMatrix(x, y) == exp)
        }

        Then("submatrix\\({mVar}, {int}, {int}) is the following 2x2 matrix:") { m: MATRIX, x: Int, y: Int, exp: MATRIX ->
            assert(m.subMatrix(x, y) == exp)
        }
        Then("inverse\\({mVar}) is the following 4x4 matrix:") { m: MATRIX, exp: MATRIX ->
            assert(m.inverse() approx exp)
        }

        Then("{mVar} is the following 4x4 matrix:") { m: MATRIX, exp: MATRIX ->
            assert(m approx exp)
        }

        Then("minor\\({mVar}, {int}, {int}) = {float}") { m: MATRIX, x: Int, y: Int, exp: Float ->
            assert(m.minor(x, y) == exp)
        }

        Then("cofactor\\({mVar}, {int}, {int}) = {float}") { m: MATRIX, x: Int, y: Int, exp: Float ->
            assert(m.cofactor(x, y) == exp)
        }

        Then("{mVar} is invertible") { m: MATRIX -> assert(m.isInvertable()) }
        Then("{mVar} is not invertible") { m: MATRIX -> assert(!m.isInvertable()) }

        Then("{mVar} * inverse\\({mVar}) = {mVar}") { mVar: MATRIX, mVar2: MATRIX, mVar3: MATRIX ->
            assert(mVar * mVar2.inverse() == mVar3)
        }

        Then("{}.origin = {tuple}") { name: String, exp: TUPLE ->
            val ray: Ray = getVar(name)
            assert(ray.origin == exp)
        }
        Then("{}.direction = {tuple}") { name: String, exp: TUPLE ->
            val ray: Ray = getVar(name)
            assert(ray.direction == exp)
        }

        Then("position\\({}, {float}) = {tuple}") { name: String, point: Float, exp: TUPLE ->
            val ray: Ray = getVar(name)
            assert(ray.position(point) == exp)

        }
    }
}