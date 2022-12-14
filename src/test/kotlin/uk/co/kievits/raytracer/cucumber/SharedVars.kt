package uk.co.kievits.raytracer.cucumber

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.EPSILON
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.base.Vector
import uk.co.kievits.raytracer.base.rotationX
import uk.co.kievits.raytracer.base.rotationY
import uk.co.kievits.raytracer.base.rotationZ
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.base.shearing
import uk.co.kievits.raytracer.base.translation
import uk.co.kievits.raytracer.cucumber.SharedVars.variablePattern
import kotlin.math.PI
import kotlin.math.sqrt

object SharedVars {
    private val numberSplitter = ", ?".toRegex()
    const val numberPattern = "((?:EPSILON|infinity|[-0-9, .√π/])+)"
    const val variablePattern = "[a-zA-Z]\\w*"

    @PublishedApi
    internal val vars = mutableMapOf<String, Any>()

    inline operator fun <reified T> get(name: String): T {
        val value = vars[name]
        assert(value is T) {
            when (value) {
                null -> "$name not found"
                else -> "$name expected ${T::class.simpleName} actual ${value::class.simpleName}"
            }
        }
        return value as T
    }

    operator fun set(
        name: String,
        value: Any?
    ) {
        when (value) {
            null -> vars.remove(name)
            else -> vars[name] = value
        }
    }

    fun parseFloats(string: String): List<V> = string.split(numberSplitter)
        .map { parseFloat(it) }

    fun parseFloat(string: String): V = when {
        string.contains("/") -> string.split("/")
            .map { parseFloat(it.trim()) }
            .reduce { a, b -> a / b }

        string.startsWith("√") -> sqrt(parseFloat(string.substring(1)))
        string.startsWith("-") -> -parseFloat(string.substring(1))
        string == "π" -> PI
        string == "EPSILON" -> EPSILON
        string == "infinity" -> V.POSITIVE_INFINITY
        else -> string.toDouble()
    }

    fun buildTuple(
        args: String?,
        type: String?,
        name: String?
    ) = when {
        args != null && type != null -> {
            val floats = parseFloats(args)
            when (type) {
                "tuple" -> TUPLE(floats[0], floats[1], floats[2], floats[3])
                "point" -> Point(floats[0], floats[1], floats[2])
                "vector" -> Vector(floats[0], floats[1], floats[2])
                "color" -> Color(floats[0], floats[1], floats[2])
                else -> TODO(type)
            }
        }

        name != null -> get(name)
        else -> TODO()
    }

    fun buildMatrix(
        name: String?,
        function: String?,
        args: String?,
    ) = when {
        name != null -> {
            when (name) {
                "identity_matrix",
                "IDENTITY_MATRIX" -> IdentityMatrix()

                else -> get<MATRIX>(name)
            }
        }

        function != null && args != null -> {
            val floats = parseFloats(args)
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

inline fun <reified T : Any> En.ParameterType(name: String) {
    ParameterType<T>(name, variablePattern) { value ->
        SharedVars.get<T>(value)
    }
}
