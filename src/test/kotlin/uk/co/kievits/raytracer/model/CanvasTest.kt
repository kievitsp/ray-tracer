package uk.co.kievits.raytracer.model

import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.junit.jupiter.api.Test
import uk.co.kievits.raytracer.model.Colors.RED

class CanvasTest {


    @Test
    fun `Creating a canvas`() {
        val c = Canvas(10, 20)
        assert(c.width == 10)
        assert(c.height == 20)
        c.forEach { assert(it == Color(0, 0, 0)) }
    }

    @Test
    fun `Writing pixels to a canvas`() {
        val c = Canvas(10, 20)
        c[2, 3] = RED
        assert(c[2, 3] == RED)

    }


    @Test
    fun `Constructing the PPM header`() {
        val c = Canvas(5, 3)
        val ppm = c.toPpm().split("\n").subList(0, 3)
        assert(
            ppm == listOf(
                "P3",
                "5 3",
                "255",
            )
        )
    }


    @Test
    fun `Constructing the PPM pixel data`() {
        val c = Canvas(5, 3)
        val c1 = Color(1.5, 0, 0)
        val c2 = Color(0, 0.5, 0)
        val c3 = Color(-0.5, 0, 1)

        c[0, 0] = c1
        c[2, 1] = c2
        c[4, 2] = c3

        val ppm = c.toPpm().split("\n")

        assert(
            ppm.subList(3, 6) == listOf(
                "255 0 0 0 0 0 0 0 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 128 0 0 0 0 0 0 0",
                "0 0 0 0 0 0 0 0 0 0 0 0 0 0 255",
            )
        )
    }

    @Test
    fun `Splitting long lines in PPM files`() {
        val c = Canvas(10, 2)
        c.setAll(Color(1, 0.8, 0.6))
        val ppm = c.toPpm().split("\n")

        assert(
            ppm.subList(3, 7) == listOf(
                "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204",
                "153 255 204 153 255 204 153 255 204 153 255 204 153",
                "255 204 153 255 204 153 255 204 153 255 204 153 255 204 153 255 204",
                "153 255 204 153 255 204 153 255 204 153 255 204 153",
            )
        )
    }

    @Test
    fun `PPM files are terminated by a newline character`() {
        val c = Canvas(5, 3)

        val ppm = c.toPpm()

        assert(ppm.last() == '\n')
    }
}