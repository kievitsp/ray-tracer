package uk.co.kievits.raytracer.base

import io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.platform.suite.api.ConfigurationParameter
import uk.co.kievits.raytracer.cucumber.Operator
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.cucumber.SharedVars.get
import uk.co.kievits.raytracer.cucumber.SharedVars.numberPattern
import uk.co.kievits.raytracer.cucumber.SharedVars.vars
import kotlin.collections.contentToString
import kotlin.collections.set

@ConfigurationParameter(
    key = GLUE_PROPERTY_NAME, value = "uk.co.kievits.shared"
)
class StepDefs : En {

    init {
        DataTableType { dataTable: DataTable ->
            val height = dataTable.height()
            val width = dataTable.width()

            val array = FloatArray(height * width) { id ->
                val row = id / width
                val column = id % width
                dataTable.row(row)[column].toFloat()
            }

            when (array.size) {
                16 -> Matrix.D4(*array)
                9 -> Matrix.D3(array)
                4 -> Matrix.D2(array)
                else -> TODO(array.contentToString())
            }
        }

        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\($numberPattern\\)|([a-z]\\w?|zero|norm|origin|direction|up|from|to)",
        ) { type: String?, args: String?, name: String? ->
            SharedVars.buildTuple(args, type, name)
        }

        ParameterType(
            "matrix",
            "(t|[A-Z]\\w*|IDENTITY_MATRIX|identity_matrix|\\w{3,})|" +
                "(translation|scaling|shearing|rotation_[xyz])\\($numberPattern\\)"
        ) { name, function, args ->
            SharedVars.buildMatrix(name, function, args)
        }

        Then("{} ← submatrix\\({matrix}, {int}, {int})") { name: String, m: MATRIX, x: Int, y: Int ->
            vars[name] = m.subMatrix(x, y)
        }

        Given("the following 2x2 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("the following 4x4 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("the following 3x3 matrix {}:") { name: String, m: MATRIX -> vars[name] = m }

        Given("the following matrix {}:") { name: String, m: MATRIX -> vars[name] = m }
        Given("{} ← transpose\\({matrix})") { name: String, m: MATRIX -> vars[name] = m.transpose }
        Given("{} ← inverse\\({matrix})") { name: String, m: MATRIX -> vars[name] = m.inverse }

        Then("{tuple}.x = {double}") { tuple: TUPLE, value: V -> assert(tuple.x == value) }
        Then("{tuple}.y = {double}") { tuple: TUPLE, value: V -> assert(tuple.y == value) }
        Then("{tuple}.z = {double}") { tuple: TUPLE, value: V -> assert(tuple.z == value) }
        Then("{tuple}.w = {double}") { tuple: TUPLE, value: V -> assert(tuple.w == value) }

        Then("{tuple}.red = {double}") { tuple: TUPLE, value: V -> assert(tuple.red == value) }
        Then("{tuple}.green = {double}") { tuple: TUPLE, value: V -> assert(tuple.green == value) }
        Then("{tuple}.blue = {double}") { tuple: TUPLE, value: V -> assert(tuple.blue == value) }

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

        Then("{tuple} {operator} {double} = {tuple}") { a: TUPLE, op: Operator, b: V, c: TUPLE ->
            assert(
                when (op) {
                    Operator.PLUS -> a + b
                    Operator.MINUS -> a - b
                    Operator.TIMES -> a * b
                    Operator.DIV -> a / b
                } == c
            )
        }

        Then("magnitude\\({tuple}) = {number}") { value: TUPLE, exp: V -> assert(value.magnitude approx exp) }
        Then("normalize\\({tuple}) = {tuple}") { value: TUPLE, exp: TUPLE -> assert(value.normalise == exp) }
        Then("normalize\\({tuple}) = approximately {tuple}") { value: TUPLE, exp: TUPLE -> assert(value.normalise approx exp) }
        When("{} ← normalize\\({tuple})") { name: String, exp: TUPLE -> vars[name] = exp.normalise }
        Then("dot\\({tuple}, {tuple}) = {double}") { a: TUPLE, b: TUPLE, exp: V -> assert(a dot b == exp) }
        Then("cross\\({tuple}, {tuple}) = {tuple}") { a: TUPLE, b: TUPLE, exp: TUPLE -> assert(a cross b == exp) }

        Then("{matrix}[{int},{int}] = {number}") { matrix: MATRIX, x: Int, y: Int, number: V ->
            assert(matrix[x, y] approx number.toFloat())
        }

        Then("{matrix} = {matrix}") { a: MATRIX, b: MATRIX -> assert(a approx b) }
        Then("{matrix} != {matrix}") { a: MATRIX, b: MATRIX -> assert(a napprox b) }

        Then("{matrix} * {matrix} is the following 4x4 matrix:") { a: Matrix<D4>, b: Matrix<D4>, c: Matrix<D4> ->
            assert(a * b == c)
        }
        Then("{matrix} * {matrix} = {matrix}") { a: Matrix<D4>, b: Matrix<D4>, c: Matrix<D4> ->
            assert(a * b == c)
        }

        Then("{matrix} * {tuple} = {tuple}") { a: MATRIX, b: TUPLE, c: TUPLE ->
            assert(a * b == c)
        }

        When("{} ← {matrix} * {tuple}") { name: String, a: MATRIX, b: TUPLE ->
            vars[name] = a * b
        }

        When("{} ← {matrix} * {matrix} * {matrix}") { name: String, a: Matrix<D4>, b: Matrix<D4>, c: Matrix<D4> ->
            vars[name] = a * b * c
        }

        When("{} ← reflect\\({tuple}, {tuple})") { name: String, a: TUPLE, b: TUPLE ->
            SharedVars[name] = a reflect b
        }

        When("{variable} ← view_transform\\({tuple}, {tuple}, {tuple})") { name: String, from: POINT, to: POINT, up: VECTOR ->
            SharedVars[name] = viewTransformation(from, to, up)
        }

        Then("transpose\\({matrix}) is the following matrix:") { a: MATRIX, b: MATRIX ->
            assert(a.transpose == b)
        }

        Then("determinant\\({matrix}) = {float}") { a: MATRIX, value: Float ->
            assert(a.determinant() == value)
        }

        Then("submatrix\\({matrix}, {int}, {int}) is the following 3x3 matrix:") { m: MATRIX, x: Int, y: Int, exp: MATRIX ->
            assert(m.subMatrix(x, y) == exp)
        }

        Then("submatrix\\({matrix}, {int}, {int}) is the following 2x2 matrix:") { m: MATRIX, x: Int, y: Int, exp: MATRIX ->
            assert(m.subMatrix(x, y) == exp)
        }
        Then("inverse\\({matrix}) is the following 4x4 matrix:") { m: MATRIX, exp: MATRIX ->
            assert(m.inverse approx exp)
        }

        Then("{matrix} is the following 4x4 matrix:") { m: MATRIX, exp: MATRIX ->
            assert(m approx exp)
        }

        Then("minor\\({matrix}, {int}, {int}) = {float}") { m: MATRIX, x: Int, y: Int, exp: Float ->
            assert(m.minor(x, y) == exp)
        }

        Then("cofactor\\({matrix}, {int}, {int}) = {float}") { m: MATRIX, x: Int, y: Int, exp: Float ->
            assert(m.cofactor(x, y) == exp)
        }

        Then("{matrix} is invertible") { m: MATRIX -> assert(m.isInvertable()) }
        Then("{matrix} is not invertible") { m: MATRIX -> assert(!m.isInvertable()) }

        Then("{matrix} * inverse\\({matrix}) = {matrix}") { matrix: Matrix<D4>, matrix2: Matrix<D4>, matrix3: Matrix<D4> ->
            assert(matrix * matrix2.inverse == matrix3)
        }

        Then("position\\({}, {double}) = {tuple}") { name: String, point: V, exp: TUPLE ->
            val ray: Ray = get(name)
            assert(ray.position(point) == exp)
        }
    }
}
