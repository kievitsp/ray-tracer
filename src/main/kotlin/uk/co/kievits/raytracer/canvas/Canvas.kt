package uk.co.kievits.raytracer.canvas

import uk.co.kievits.raytracer.base.COLOR
import java.io.OutputStream

interface Canvas {
    val width: Int
    val height: Int
    operator fun set(x: Int, y: Int, color: COLOR)

    fun write(outputStream: OutputStream)
}
