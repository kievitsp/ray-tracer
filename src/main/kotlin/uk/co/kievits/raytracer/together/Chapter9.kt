package uk.co.kievits.raytracer.together

import kotlinx.coroutines.runBlocking
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Colors.BLACK
import uk.co.kievits.raytracer.base.Colors.WHITE
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Vector
import uk.co.kievits.raytracer.base.rotationX
import uk.co.kievits.raytracer.base.rotationY
import uk.co.kievits.raytracer.base.rotationZ
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.base.translation
import uk.co.kievits.raytracer.base.viewTransformation
import uk.co.kievits.raytracer.canvas.ImageType
import uk.co.kievits.raytracer.dsl.world
import uk.co.kievits.raytracer.material.CheckeredPattern
import uk.co.kievits.raytracer.material.StripedPattern
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
        val floor = plane {
            material {
                val newPattern = (StripedPattern(BLACK, WHITE) + StripedPattern(BLACK, WHITE))
                pattern = (
                    StripedPattern(BLACK, WHITE) + StripedPattern(BLACK, WHITE).apply {
                        transform = rotationZ(PI / 4)
                    }
                    ) / 2
                specular = 0f
            }
        }

        plane {
            transform = translation(0, 0, 5) *
                rotationY(-PI / 4) * rotationX(PI / 2)
            material = floor.material
        }

        plane {
            transform = translation(0, 0, 5) *
                rotationY(PI / 4) * rotationX(PI / 2)
            material = floor.material
        }

        sphere {
            transform = translation(-0.5, 1, 0.5)
            material {
                pattern = StripedPattern(Color(0.1, 1, .5), Color(0.1, 0, .5)).apply {
                    transform = rotationY(PI / 3) * rotationZ(PI / 5) *
                        scaling(0.1, 0.1, 0.1)
                }.perturbed()
                diffuse = 0.7f
                specular = 0.3f
            }
        }

        sphere {
            transform = translation(1.5, 0.5, -.5) * scaling(0.5, 0.5, 0.5)
            material {
                pattern = CheckeredPattern(
                    Color(0.5, 1, .1),
                    Color(0.5, 0, .9),
                ).apply {
                    transform = scaling(.1, .1, .1)
                }
                diffuse = 0.7f
                specular = 0.3f
            }
        }

        sphere {
            transform = translation(-1.5, 0.33, -.75) * scaling(.33, .33, .33)
            material {
                color = Color(1, 0.8, 0.1)
                diffuse = 0.7f
                specular = 0.3f
            }
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
        runBlocking { camera.render(world, ImageType.PNG) }
    }

    println("async time $aSyncTime")

    val path = Paths.get("./chapter9.png")

    Files.newOutputStream(path).use {
        image.write(it)
    }

    exitProcess(0)
}
