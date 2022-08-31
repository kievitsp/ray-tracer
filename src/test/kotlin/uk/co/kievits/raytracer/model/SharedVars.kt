package uk.co.kievits.raytracer.model

import kotlin.math.PI
import kotlin.math.sqrt

object SharedVars {
    val numberSplitter = ", ?".toRegex()
    val vars = mutableMapOf<String, Any>()

    inline fun <reified T> getVar(name: String): T {
        val value = vars[name] as? T
        assert(value is T) { vars[name].toString() }
        return value as T
    }

    fun parseFloats(string: String): List<Float> = string.split(numberSplitter)
        .map { parseFloat(it) }

    fun parseFloat(string: String): Float {
        return when {
            string.contains("/") -> string.split("/")
                .map { parseFloat(it.trim()) }
                .reduce { a, b -> a / b }

            string.startsWith("√") -> sqrt(parseFloat(string.substring(1)))
            string.startsWith("-") -> -parseFloat(string.substring(1))
            string == "π" -> PI.toFloat()
            else -> string.toFloat()
        }
    }

}