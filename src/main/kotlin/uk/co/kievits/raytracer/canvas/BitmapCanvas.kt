package uk.co.kievits.raytracer.canvas

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.bitValue
import java.awt.image.BufferedImage
import java.io.BufferedOutputStream
import java.io.OutputStream
import javax.imageio.ImageIO

class BitmapCanvas(
    override val width: Int,
    override val height: Int,
) : Canvas {

    private val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    override fun set(x: Int, y: Int, color: COLOR) {
        image.setRGB(x, y, color.rgb())
    }

    override tailrec fun write(outputStream: OutputStream) {
        if (outputStream !is BufferedOutputStream) return write(BufferedOutputStream(outputStream))
        ImageIO.write(image, "PNG", outputStream)
    }

    private fun COLOR.rgb(): Int = intColor(red.bitValue, green.bitValue, blue.bitValue)

    private fun intColor(
        red: Int,
        green: Int,
        blue: Int
    ): Int = (red shl 16) +
        (green shl 8) +
        (blue)
}
