package uk.co.kievits.raytracer.model

import io.cucumber.java8.En
import uk.co.kievits.raytracer.model.SharedVars.getVar
import uk.co.kievits.raytracer.model.SharedVars.numberSplitter
import uk.co.kievits.raytracer.model.SharedVars.vars

class CanvasSteps : En {
    lateinit var canvas: Canvas

    init {
        ParameterType(
            "canvas",
            "canvas\\((.*?)\\)",
        ) { args: String ->
            val floats = args.split(numberSplitter).map { it.toInt() }
            Canvas(floats[0], floats[1])
        }
        Given("c ← {canvas}") { canvas: CANVAS ->
            this.canvas = canvas
        }

        When("write_pixel\\(c, {int}, {int}, {})") {  x: Int, y: Int, color: String ->//, c: COLOR ->
            canvas[x, y] = getVar(color)
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
            assert(canvas[x, y] == getVar(color))
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

    }
}