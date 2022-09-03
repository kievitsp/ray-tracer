package uk.co.kievits.raytracer.together

import uk.co.kievits.raytracer.base.Canvas
import uk.co.kievits.raytracer.base.Colors
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.model.Sphere
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val sphere = Sphere()
    val canvas = Canvas(800, 800)
    val cameraPoint = Point(0, 0, -2)
    var hits = 0

    val end = 400
    val start = -end

//    val cStart = -20
//    val cEnd = 20

    val cStart = start
    val cEnd = end

    for (x in cStart until cEnd) {
        for (y in cStart until cEnd) {
            val point = Point(x, y, 200)
            val vector = point - cameraPoint
            val hit = sphere.intersections(Ray(origin = cameraPoint, direction = vector)).hit() != null

            if (hit) {
                hits++
                canvas[x + end, y + end] = Colors.RED
            }
        }
    }

    val path = Paths.get("./chapter5.ppm")

    Files.newBufferedWriter(path).use {
        it.write(canvas.toPpm())
    }
}