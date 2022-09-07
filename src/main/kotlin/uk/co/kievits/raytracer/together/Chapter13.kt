package uk.co.kievits.raytracer.together

import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Colors.BLACK
import uk.co.kievits.raytracer.base.Colors.RED
import uk.co.kievits.raytracer.base.Colors.WHITE
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Vector
import uk.co.kievits.raytracer.base.rotationX
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.base.translation
import uk.co.kievits.raytracer.base.viewTransformation
import uk.co.kievits.raytracer.canvas.ImageType
import uk.co.kievits.raytracer.dsl.world
import uk.co.kievits.raytracer.material.CheckeredPattern
import uk.co.kievits.raytracer.world.Camera
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.PI
import kotlin.math.min
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue
import kotlinx.coroutines.runBlocking
import uk.co.kievits.raytracer.base.Colors.BLUE
import uk.co.kievits.raytracer.base.Colors.GREEN

@OptIn(ExperimentalTime::class)
fun main() {
    val world = world {
        cube {
            transform = scaling(20)
            material {
                pattern = CheckeredPattern(WHITE, BLACK).apply {
                    transform = scaling(.05)
                }
                reflective = 0.03
            }
        }

        cone {
            maximum = 0
            minimum = -1
            transform = translation(y = 3)

            material {
                color = RED
                reflective = 0.03
            }
        }
        cylinder {
            maximum = 2
            minimum = -1
            material = glass
        }
        cube {
            transform = translation(y = -2)
            material {
                color = BLUE
                reflective = .25
            }
        }
    }

    val camera = Camera(
        hSize = 800,
        vSize = 600,
        fieldOfView = PI / 2,
    )

    camera.transform = viewTransformation(
        Point(0, 1.5, -5),
        Point(0, 1, 0),
        Vector(0, 1, 0)
    )

    val (image, aSyncTime) = measureTimedValue {
//        Thread.sleep(10_000)
//        camera.render(world, ImageType.PNG)
        runBlocking {
//            delay(10_000)
            camera.renderAsync(world, ImageType.PNG)
        }
    }

    println("async time $aSyncTime")

    val path = Paths.get("./chapter13.png")

    Files.newOutputStream(path).use {
        image.write(it)
    }

    exitProcess(0)
}
