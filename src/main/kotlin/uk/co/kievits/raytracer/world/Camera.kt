package uk.co.kievits.raytracer.world

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.IdentityMatrix
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.canvas.Canvas
import uk.co.kievits.raytracer.canvas.ImageType
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.math.tan

class Camera(
    val hSize: Int,
    val vSize: Int,
    val fieldOfView: V,
    transform: Matrix<D4> = IdentityMatrix(),
) {
    var transform: Matrix<D4> = transform
        set(value) {
            if (value == field) return
            field = value
            origin = transform.inverse * Point(0f, 0f, 0f)
        }

    private var origin = this.transform.inverse * Point(0f, 0f, 0f)

    val pixelSize: V
    val halfWidth: V
    val halfHeight: V

    init {
        val halfView = tan(fieldOfView / 2)
        val aspect = hSize / vSize.toFloat()

        when (aspect >= 1) {
            true -> {
                halfWidth = halfView
                halfHeight = halfView / aspect
            }

            false -> {
                halfWidth = halfView * aspect
                halfHeight = halfView
            }
        }

        pixelSize = (halfWidth * 2) / hSize
    }

    fun rayForPixel(px: Int, py: Int): Ray {
        val xOffset: V = (px + .5f) * pixelSize
        val yOffset: V = (py + .5f) * pixelSize

        val worldX: V = halfWidth - xOffset
        val worldY: V = halfHeight - yOffset

        val pixel = transform.inverse * Point(worldX, worldY, -1f)
//        val origin = inverseTransform * Point(0f, 0f, 0f)

        val direction = (pixel - origin).normalise

        return Ray(origin, direction)
    }

    fun <C : Canvas> render(
        world: World,
        imageType: ImageType<C>,
    ): C = render(world, imageType(hSize, vSize))

    fun render(
        world: World
    ) = render(world, ImageType.PPM)

    fun <C : Canvas> render(
        world: World,
        image: C,
    ): C {
        for (y in 0 until vSize) {
            for (x in 0 until hSize) {
                val ray = rayForPixel(x, y)
                val color = world.colorAt(ray)
                image[x, y] = color
            }
        }

        return image
    }

    suspend fun <C : Canvas> renderAsync(
        world: World,
        imageType: ImageType<C>,
    ): C = renderAsync(world, imageType(hSize, vSize))

    suspend fun <C : Canvas> renderAsync(
        world: World,
        image: C,
    ): C {
        sequence {
            for (y in 0 until vSize) {
                for (x in 0 until hSize) {
                    yield(CanvasPixel(x, y))
                }
            }
        }.chunked(1000)
            .asFlow()
            .flowOn(coroutineContext)
            .map { list ->
                async {
//                    println("Staring job ${list.firstOrNull()}")
                    list.map { pixel ->
                        val ray = rayForPixel(pixel.x, pixel.y)
                        pixel.withColor(world.colorAt(ray))
                    }
                }
            }
            .buffer(cpus * 4)
            .map { it.await() }
            .collect { list ->
                list.forEach { (x, y, color) ->
                    image[x, y] = color
                }
            }

        return image
    }

    private data class CanvasPixel(val x: Int, val y: Int) {
        fun withColor(color: COLOR) = CanvasPixelWithColor(x, y, color)
    }

    private data class CanvasPixelWithColor(val x: Int, val y: Int, val color: COLOR)

    companion object : CoroutineScope {
        private val cpus = Runtime.getRuntime().availableProcessors()
        override val coroutineContext: CoroutineContext =
            Executors.newFixedThreadPool(cpus).asCoroutineDispatcher()
    }
}
