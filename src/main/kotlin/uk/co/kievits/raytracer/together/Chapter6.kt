package uk.co.kievits.raytracer.together

import uk.co.kievits.raytracer.base.Canvas
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.model.Sphere
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val sphere = Sphere()
    sphere.material.color = Color(1, .2, 1)
    val canvas = Canvas(800, 800)
    val cameraPoint = Point(0, 0, -1.5)
    val light = PointLight(Point(-10, 10, -10), Color(.5, .5, .5))
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
            val ray = Ray(origin = cameraPoint, direction = vector)
            val hit = sphere.intersections(ray).hit()

            if (hit != null) {
                hits++
                val material = hit.shape.material
                val point = ray.position(hit.t)
                val normal = hit.shape.normalAt(point)
                val eye = -ray.direction
                val color = material.lighting(light, point, eye, normal, false)
                canvas[x + end, y + end] = color
            }
        }
    }

    val path = Paths.get("./chapter6.ppm")

    Files.newBufferedWriter(path).use {
        it.write(canvas.toPpm())
    }
}
