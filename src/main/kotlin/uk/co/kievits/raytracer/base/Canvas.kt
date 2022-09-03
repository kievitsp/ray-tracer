package uk.co.kievits.raytracer.base

import kotlin.math.roundToInt
import kotlin.text.StringBuilder

class Canvas(
    val width: Int,
    val height: Int,
) {
    private val data = Array(height * width) {
        Colors.BLACK
    }

    operator fun set(x: Int, y: Int, color: COLOR) {
        require(x in 0 until width) { "$x, max $width" }
        require(y in 0 until height) { "$y, max $height" }
        data[x + y * width] = color
    }

    operator fun get(x: Int, y: Int): COLOR {
        require(x in 0 until width) { "$x, max $width" }
        require(y in 0 until height) { "$y, max $height" }
        return data[x + y * width]
    }

    fun toPpm(): String {
        val builder = StringBuilder()

        builder.append("P3\n$width $height\n255")

        val lastPixel = width - 1

        for (y in 0 until height) {
            builder.append('\n')
            var size = 0
            for (x in 0 until width) {
                val color = get(x, y)
                size = builder.appendColor(color.red.bitValue, size, true)
                size = builder.appendColor(color.green.bitValue, size, true)
                size = builder.appendColor(color.blue.bitValue, size, x != lastPixel)
            }
        }
        builder.append('\n')
        return builder.toString()
    }

    private fun StringBuilder.appendColor(
        value: Int,
        previousSize: Int,
        addSpace: Boolean
    ): Int {
        var lineSize = previousSize
        val numberSize = when {
            value < 10 -> 1
            value < 100 -> 2
            else -> 3
        }

        if (lineSize + numberSize >= 70) {
            append('\n')
            lineSize = 0
        } else if (lineSize > 0) {
            append(' ')
            lineSize += 1
        }

        append(value)

        return lineSize + numberSize
    }

    private val V.bitValue: Int
        get() = when {
            this > 1.0 -> 255
            this < 0 -> 0
            else -> (this * 255.0).roundToInt()
        }

    private fun ppmPixel(x: Int, y: Int): String = get(x, y).ppmPixel()

    private fun COLOR.ppmPixel(): String = "${red.bitValue} ${green.bitValue} ${blue.bitValue}"

    internal inline fun forEach(block: (COLOR) -> Unit) {
        data.forEach {
            block(it)
        }
    }

    internal fun setAll(color: COLOR) {
        data.indices.forEach { data[it] = color.copy() }
    }
}