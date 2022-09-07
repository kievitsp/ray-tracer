package uk.co.kievits.raytracer.canvas

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.V
import java.io.OutputStream
import kotlin.math.roundToInt

abstract class Canvas {
    abstract val width: Int
    abstract val height: Int
    abstract operator fun set(x: Int, y: Int, color: COLOR)

    abstract fun write(outputStream: OutputStream)

    protected val V.bitValue: Int
        get() = when {
            this > 1.0 -> 255
            this < 0 -> 0
            else -> (this * 255.0).roundToInt()
        }
}
