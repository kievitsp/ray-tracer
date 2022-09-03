package uk.co.kievits.raytracer.base

import io.cucumber.java8.En
import uk.co.kievits.raytracer.model.SharedVars
import uk.co.kievits.raytracer.model.SharedVars.get
import uk.co.kievits.raytracer.model.SharedVars.vars

class CanvasSteps : En {
    lateinit var canvas: Canvas

    init {
        ParameterType(
            "canvas",
            "canvas\\((.*?)\\)",
        ) { args: String ->
            val ints = SharedVars.parseFloats(args).map { it.toInt() }
            Canvas(ints[0], ints[1])
        }
        Given("c ← {canvas}") { canvas: CANVAS ->
            this.canvas = canvas
        }

        When("write_pixel\\(c, {int}, {int}, {})") { x: Int, y: Int, color: String -> // , c: COLOR ->
            canvas[x, y] = get(color)
        }

        When("{} ← canvas_to_ppm\\(c)") { name: String ->
            vars[name] = canvas.toPpm()
        }

        When("every pixel of c is set to {tuple}") { color: COLOR ->
            for (x in 0 until canvas.width) {
                for (y in 0 until canvas.height) {
                    canvas[x, y] = color
                }
            }
        }

        Then("pixel_at\\(c, {int}, {int}) = {}") { x: Int, y: Int, color: String ->
            assert(canvas[x, y] == get(color))
        }

        Then("c.width = {int}") { value: Int -> assert(canvas.width == value) }
        Then("c.height = {int}") { value: Int -> assert(canvas.height == value) }

        Then("every pixel of c is {tuple}") { color: COLOR ->
            for (x in 0 until canvas.width) {
                for (y in 0 until canvas.height) {
                    assert(canvas[x, y] == color)
                }
            }
        }

        Then("lines {int}-{int} of {stringVar} are") { start: Int, end: Int, ppm: String, expected: String ->
            assert(ppm.split("\n").subList(start - 1, end).joinToString("\n") == expected)
        }

        Then("{stringVar} ends with a newline character") { ppm: String ->
            assert(ppm.lastOrNull() == '\n')
        }
    }
}
