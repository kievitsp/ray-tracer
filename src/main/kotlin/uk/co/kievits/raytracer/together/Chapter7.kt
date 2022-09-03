package uk.co.kievits.raytracer.together

import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Vector
import uk.co.kievits.raytracer.base.rotationX
import uk.co.kievits.raytracer.base.rotationY
import uk.co.kievits.raytracer.base.scaling
import uk.co.kievits.raytracer.base.translation
import uk.co.kievits.raytracer.base.viewTransformation
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.model.Sphere
import uk.co.kievits.raytracer.world.Camera
import uk.co.kievits.raytracer.world.World
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.math.PI
import kotlin.system.exitProcess
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
fun main() {
    val floor = Sphere().apply {
        transform = scaling(10, 0.01, 10)
        material.apply {
            color = Color(1, 0.9, 0.9)
            specular = 0f
        }
    }

    val leftWall = Sphere().apply {
        transform = translation(0, 0, 5) *
                rotationY(-PI / 4) * rotationX(PI / 2) *
                scaling(10, 0.01, 10)
        material = floor.material
    }

    val rightWall = Sphere().apply {
        transform = translation(0, 0, 5) *
                rotationY(PI / 4) * rotationX(PI / 2) *
                scaling(10, 0.01, 10)
        material = floor.material
    }

    val middle = Sphere().apply {
        transform = translation(-0.5, 1, 0.5)
        material.apply {
            color = Color(0.1, 1, .5)
            diffuse = 0.7f
            specular = 0.3f
        }
    }

    val right = Sphere().apply {
        transform = translation(1.5, 0.5, -.5) * scaling(0.5, 0.5, 0.5)
        material.apply {
            color = Color(0.5, 1, .1)
            diffuse = 0.7f
            specular = 0.3f
        }
    }

    val left = Sphere().apply {
        transform = translation(-1.5, 0.33, -.75) * scaling(.33, .33, .33)
        material.apply {
            color = Color(1, 0.8, 0.1)
            diffuse = 0.7f
            specular = 0.3f
        }
    }

    val world = World(
        shapes = mutableListOf(
            floor,
            leftWall,
            rightWall,
            middle,
            right,
            left,
        ),
        light = PointLight(Point(-10, 10, -10), Color(1, 1, 1))
    )

    val camera = Camera(
        1600,
        1600,
        (PI / 3).toFloat(),
    )

    camera.transform = viewTransformation(
        Point(0, 1.5, -5),
        Point(0, 1, 0),
        Vector(0, 1, 0)
    )

    val (image, time) = measureTimedValue {
        camera.render(world)
    }


    // 50.603431300s
    println("normal time $time")

    val (_, aSyncTime) = measureTimedValue {
        runBlocking { camera.renderAsync(world) }
    }

    // 17.521477800s
    println("async time $aSyncTime")

    val path = Paths.get("./chapter7.ppm")

    Files.newBufferedWriter(path).use {
        image.toPpm(it)
    }

    exitProcess(0)
}
