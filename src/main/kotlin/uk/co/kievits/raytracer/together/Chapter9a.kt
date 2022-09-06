package uk.co.kievits.raytracer.together

import kotlinx.coroutines.runBlocking
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
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main() {
    val world = world {
        plane {
            transform = translation(z = 5) * rotationX(PI / 2)
            material {
                pattern = CheckeredPattern(WHITE, BLACK).apply {
                    transform = scaling(0.1, 0.1, .1)
                }
                reflective = .05
            }
        }

        sphere {
            transform = translation(x = -0, y = 1) * scaling(1.5)
            material = glass
        }

        sphere {
            transform = translation(x = 2, y = 2) * scaling(.5)
            material = mirror
        }

        sphere {
            transform = translation(x = 2, y = 2, z = 5) * scaling(.5)
            material {
                color = RED
            }
        }

        cube {
            transform = translation(x = -2, y = 2) * scaling(.5)
            material = mirror
        }
    }

    val camera = Camera(
        hSize = 800,
        vSize = 600,
        fieldOfView = (PI / 3).toFloat(),
    )

    camera.transform = viewTransformation(
        Point(0, 1.5, -5),
        Point(0, 1, 0),
        Vector(0, 1, 0)
    )

    val (image, aSyncTime) = measureTimedValue {
        runBlocking { camera.renderAsync(world, ImageType.PNG) }
    }

    println("async time $aSyncTime")

    val path = Paths.get("./chapter9a.png")

    Files.newOutputStream(path).use {
        image.write(it)
    }

    exitProcess(0)
}
