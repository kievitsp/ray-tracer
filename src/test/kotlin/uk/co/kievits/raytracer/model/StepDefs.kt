package uk.co.kievits.raytracer.model

import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import io.cucumber.java8.PendingException
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.sqrt


class StepDefs : En {
    private val vars = mutableMapOf<String, Any>()

    enum class Operator {
        PLUS, MINUS, TIMES, DIV,
    }

    private inline fun <reified T> getVar(name: String): T {
        val value = vars[name] as? T
        assert(value is T) { vars[name].toString() }
        return value as T
    }

    init {
        val numberSplitter = ", ?".toRegex()

        DataTableType { dataTable: DataTable ->
            val height = dataTable.height()
            val rows = (0 until height).map { row ->
                dataTable.row(row).map { cells -> cells.toDouble() }.toDoubleArray()
            }.toTypedArray()
            mk.ndarray(rows)
        }

        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\((.*?)\\)|([a-z]\\w*)",
        ) { type: String?, args: String?, name: String? ->
            if (args != null && type != null) {
                val doubles = args.split(numberSplitter).map { it.toDouble() }
                when (type) {
                    "tuple" -> Tuple(doubles[0], doubles[1], doubles[2], doubles[3])
                    "point" -> Point(doubles[0], doubles[1], doubles[2])
                    "vector" -> Vector(doubles[0], doubles[1], doubles[2])
                    "color" -> Color(doubles[0], doubles[1], doubles[2])
                    else -> TODO(type)
                }
            } else if (name != null) {
                getVar<TUPLE>(name)
            } else {
                TODO()
            }
        }

        ParameterType(
            "canvas",
            "canvas\\((.*?)\\)|([a-z]\\w*)",
        ) { args: String?, second: String? ->
            if (args != null) {
                val doubles = args.split(numberSplitter).map { it.toInt() }
                Canvas(doubles[0], doubles[1])
            } else if (second != null) {
                getVar<CANVAS>(second)
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
            "mVar", "[A-Z]\\w*|IDENTITY_MATRIX"
        ) { name ->
            when (name) {
                "IDENTITY_MATRIX" -> IdentityMatrix()
                else -> getVar<MATRIX>(name)
            }
        }

        ParameterType(
            "number", "(√)?(-?\\d+(\\.\\d+)?)|(-?\\d+/-?\\d+)"
        ) { sqrt: String?, value: String?, division: String? ->
            if (value != null) {
                val double = value.toDouble()
                if (sqrt == null) double else sqrt(double)
            } else if (division != null) {
                val doubles = division.split('/').map { it.toDouble() }
                doubles[0] / doubles[1]
            } else {
                TODO()
            }
        }

        Given("{} ← {tuple}") { name: String, tuple: TUPLE ->
            vars[name] = tuple
        }

        Given("{} ← {canvas}") { name: String, canvas: CANVAS ->
            vars[name] = canvas
        }

        Then("{} ← submatrix\\({mVar}, {int}, {int})") { name: String, m: MATRIX, x: Int, y: Int ->
            vars[name] = m.subMatrix(x, y)
        }

        Given("the following 2x2 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("the following 4x4 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("the following 3x3 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("the following matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("{} ← transpose\\({mVar})") { name: String, m: MATRIX -> vars[name] = m.transpose() }
        Given("{} ← inverse\\({mVar})") { name: String, m: MATRIX -> vars[name] = m.inverse() }

        Then("{tuple}.x = {double}") { tuple: TUPLE, value: Double -> assert(tuple.x == value) }
        Then("{tuple}.y = {double}") { tuple: TUPLE, value: Double -> assert(tuple.y == value) }
        Then("{tuple}.z = {double}") { tuple: TUPLE, value: Double -> assert(tuple.z == value) }
        Then("{tuple}.w = {double}") { tuple: TUPLE, value: Double -> assert(tuple.w == value) }

        Then("{tuple}.red = {double}") { tuple: TUPLE, value: Double -> assert(tuple.red == value) }
        Then("{tuple}.green = {double}") { tuple: TUPLE, value: Double -> assert(tuple.green == value) }
        Then("{tuple}.blue = {double}") { tuple: TUPLE, value: Double -> assert(tuple.blue == value) }

        Then("{canvas}.width = {int}") { canvas: CANVAS, value: Int -> assert(canvas.width == value) }
        Then("{canvas}.height = {int}") { canvas: CANVAS, value: Int -> assert(canvas.height == value) }

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

        Then("{tuple} {operator} {double} = {tuple}") { a: TUPLE, op: Operator, b: Double, c: TUPLE ->
            assert(
                when (op) {
                    Operator.PLUS -> a + b
                    Operator.MINUS -> a - b
                    Operator.TIMES -> a * b
                    Operator.DIV -> a / b
                } == c
            )
        }

        Then("magnitude\\({tuple}) = {number}") { value: TUPLE, exp: Double -> assert(value.magnitude == exp) }
        Then("normalize\\({tuple}) = {tuple}") { value: TUPLE, exp: TUPLE -> assert(value.normalise == exp) }
        Then("normalize\\({tuple}) = approximately {tuple}") { value: TUPLE, exp: TUPLE -> assert(value.normalise approx exp) }
        When("{} ← normalize\\({tuple})") { name: String, exp: TUPLE -> vars[name] = exp.normalise }
        Then("dot\\({tuple}, {tuple}) = {double}") { a: TUPLE, b: TUPLE, exp: Double -> assert(a dot b == exp) }
        Then("cross\\({tuple}, {tuple}) = {tuple}") { a: TUPLE, b: TUPLE, exp: TUPLE -> assert(a cross b == exp) }

        When("write_pixel\\({canvas}, {int}, {int}, {tuple})") { canvas: CANVAS, x: Int, y: Int, c: COLOR ->
            canvas[x, y] = c
        }

        When("{} ← canvas_to_ppm\\({canvas})") { name: String, canvas: CANVAS ->
            vars[name] = canvas.toPpm()
        }

        When("every pixel of {canvas} is set to {tuple}") { canvas: CANVAS, color: COLOR ->
            for (x in 0 until canvas.width) {
                for (y in 0 until canvas.height) {
                    canvas[x, y] = color
                }
            }
        }

        Then("pixel_at\\({canvas}, {int}, {int}) = {tuple}") { canvas: CANVAS, x: Int, y: Int, c: COLOR ->
            assert(canvas[x, y] == c)
        }

        Then("lines {int}-{int} of {stringVar} are") { start: Int, end: Int, ppm: String, expected: String ->
            assert(ppm.split("\n").subList(start - 1, end).joinToString("\n") == expected)
        }

        Then("every pixel of {canvas} is {tuple}") { canvas: CANVAS, color: COLOR ->
            for (x in 0 until canvas.width) {
                for (y in 0 until canvas.height) {
                    assert(canvas[x, y] == color)
                }
            }
        }

        Then("{stringVar} ends with a newline character") { ppm: String ->
            assert(ppm.lastOrNull() == '\n')
        }

        Then("{mVar}[{int},{int}] = {number}") { matrix: MATRIX, x: Int, y: Int, number: Double ->
            assert(matrix[x, y] == number)
        }

        Then("{mVar} = {mVar}") { a: MATRIX, b: MATRIX -> assert(a == b) }
        Then("{mVar} != {mVar}") { a: MATRIX, b: MATRIX -> assert(a != b) }

        Then("{mVar} * {mVar} is the following 4x4 matrix:") { a: MATRIX, b: MATRIX, c: MATRIX ->
            assert(a * b == c)
        }

        Then("{mVar} * {mVar} = {mVar}") { a: MATRIX, b: MATRIX, c: MATRIX ->
            assert(a * b == c)
        }

        Then("{mVar} * {tuple} = {tuple}") { a: MATRIX, b: TUPLE, c: TUPLE ->
            assert(a * b == c)
        }

        Then("transpose\\({mVar}) is the following matrix:") { a: MATRIX, b: MATRIX ->
            assert(a.transpose() == b)
        }

        Then("determinant\\({mVar}) = {double}") { a: MATRIX, value: Double ->
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

        Then("minor\\({mVar}, {int}, {int}) = {double}") { m: MATRIX, x: Int, y: Int, exp: Double ->
            assert(m.minor(x, y) == exp)
        }

        Then("cofactor\\({mVar}, {int}, {int}) = {double}") { m: MATRIX, x: Int, y: Int, exp: Double ->
            assert(m.cofactor(x, y) == exp)
        }

        Then("{mVar} is invertible") { m: MATRIX -> assert(m.isInvertable()) }
        Then("{mVar} is not invertible") { m: MATRIX -> assert(!m.isInvertable()) }
    }
}