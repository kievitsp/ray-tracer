package uk.co.kievits.raytracer.canvas

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Colors
import java.io.BufferedWriter
import java.io.CharArrayWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer

class PpmCanvas(
    override val width: Int,
    override val height: Int,
) : Canvas() {
    private val data = Array(height * width) {
        Colors.BLACK
    }

    override operator fun set(x: Int, y: Int, color: COLOR) {
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
        val writer = CharArrayWriter()
        toPpm(writer)
        return writer.toString()
    }

    override fun write(
        outputStream: OutputStream
    ) = toPpm(BufferedWriter(OutputStreamWriter(outputStream)))

    fun toPpm(writer: Writer) {
        writer.write("P3\n$width $height\n255")

        for (y in 0 until height) {
            writer.write("\n")
            var size = 0
            for (x in 0 until width) {
                val color = get(x, y)
                size = writer.appendColor(color.red.bitValue, size)
                size = writer.appendColor(color.green.bitValue, size)
                size = writer.appendColor(color.blue.bitValue, size)
            }
        }
        writer.write("\n")
    }

    private fun Writer.appendColor(
        value: Int,
        previousSize: Int
    ): Int {
        var lineSize = previousSize
        val numberSize = when {
            value < 10 -> 1
            value < 100 -> 2
            else -> 3
        }

        if (lineSize + numberSize >= 70) {
            write("\n")
            lineSize = 0
        } else if (lineSize > 0) {
            write(" ")
            lineSize += 1
        }

        write(value.toString())

        return lineSize + numberSize
    }
}
