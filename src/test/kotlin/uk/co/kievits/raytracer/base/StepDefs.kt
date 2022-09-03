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
                16 -> Matrix.D4(array)
                9 -> Matrix.D3(array)
                4 -> Matrix.D2(array)
                else -> TODO(array.contentToString())
            }
        }

        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\($numberPattern\\)|([a-z]\\w?|zero|norm|origin|direction)",
        ) { type: String?, args: String?, name: String? ->
            SharedVars.buildTuple(args, type, name)
        }

        ParameterType(
            "mVar",
            "(t|[A-Z]\\w*|IDENTITY_MATRIX|identity_matrix|\\w{3,})|" +
                "(translation|scaling|shearing|rotation_[xyz])\\($numberPattern\\)"
        ) { name, function, args ->
            SharedVars.buildMatrix(name, function, args)
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

        When("{} ← reflect\\({tuple}, {tuple})") { name: String, a: TUPLE, b: TUPLE ->
            SharedVars[name] = a reflect b
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

        Then("position\\({}, {float}) = {tuple}") { name: String, point: Float, exp: TUPLE ->
            val ray: Ray = get(name)
            assert(ray.position(point) == exp)
        }
    }
}
