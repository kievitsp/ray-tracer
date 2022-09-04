package uk.co.kievits.raytracer.camera

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.base.approx
import uk.co.kievits.raytracer.base.viewTransformation
import uk.co.kievits.raytracer.canvas.PpmCanvas
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.world.Camera
import uk.co.kievits.raytracer.world.World

class CameraSteps : En {
    private lateinit var camera: Camera
    private lateinit var ray: Ray
    private lateinit var image: PpmCanvas

    init {
        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\(${SharedVars.numberPattern}\\)|([a-z]\\w?|zero|norm|origin|direction|up|from|to)",
        ) { type: String?, args: String?, name: String? ->
            SharedVars.buildTuple(args, type, name)
        }

        ParameterType(
            "matrix",
            "(m\\w*|t|IDENTITY_MATRIX|identity_matrix|)|" +
                "(translation|scaling|shearing|rotation_[xyz])\\(${SharedVars.numberPattern}\\)"
        ) { name, function, args ->
            SharedVars.buildMatrix(name, function, args)
        }

        ParameterType(
            "nVar",
            "[hv]size|field_of_view"
        ) { name: String ->
            SharedVars.get<Float>(name)
        }

        Given("{variable} ← {number}") { name: String, value: Float ->
            SharedVars[name] = value
        }

        When("c ← camera\\({nVar}, {nVar}, {nVar})") { hsize: Float, vsize: Float, viewOfView: Float ->
            camera = Camera(
                hSize = hsize.toInt(),
                vSize = vsize.toInt(),
                fieldOfView = viewOfView
            )
        }

        When("c ← camera\\({number}, {number}, {number})") { hsize: Float, vsize: Float, viewOfView: Float ->
            camera = Camera(
                hSize = hsize.toInt(),
                vSize = vsize.toInt(),
                fieldOfView = viewOfView
            )
        }

        When("r ← ray_for_pixel\\(c, {int}, {int})") { px: Int, py: Int ->
            ray = camera.rayForPixel(px, py)
        }

        When("c.transform ← {matrix} * {matrix}") { a: Matrix<D4>, b: Matrix<D4> ->
            camera.transform = a * b
        }

        When("c.transform ← view_transform\\({tuple}, {tuple}, {tuple})") { from: POINT, to: POINT, up: VECTOR ->
            camera.transform = viewTransformation(from, to, up)
        }

        When("image ← render\\(c, {world})") { world: World ->
            image = camera.render(world)
        }

        Then("c.hsize = {number}") { expected: Float -> assert(camera.hSize == expected.toInt()) }
        Then("c.vsize = {number}") { expected: Float -> assert(camera.vSize == expected.toInt()) }
        Then("c.field_of_view = {number}") { expected: Float -> assert(camera.fieldOfView == expected) }
        Then("c.pixel_size = {number}") { expected: Float -> assert(camera.pixelSize approx expected) }
        Then("c.transform = {matrix}") { expected: MATRIX -> assert(camera.transform == expected) }

        Then("r.origin = {tuple}") { exp: TUPLE -> assert(ray.origin == exp) }
        Then("r.direction = {tuple}") { exp: TUPLE -> assert(ray.direction == exp) }

        Then("pixel_at\\(image, {int}, {int}) = {tuple}") { x: Int, y: Int, color: COLOR ->
            assert(image[x, y] == color)
        }
    }
}
