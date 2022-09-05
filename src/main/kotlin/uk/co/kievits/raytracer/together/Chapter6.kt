package uk.co.kievits.raytracer.together

import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.canvas.PpmCanvas
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.shape.Sphere
import java.nio.file.Files
import java.nio.file.Paths

fun main() {
    val sphere = Sphere()
    sphere.material.color = Color(1, .2, 1)
    val canvas = PpmCanvas(800, 600)
    val cameraPoint = Point(0, 0, -1.5)
    val light = PointLight(Point(-10, 10, -10), Color(.5, .5, .5))
    var hits = 0

    val midHeight = canvas.height / 2
    val midWidth = canvas.width / 2

    for (x in -midHeight until midHeight) {
        for (y in -midWidth until midWidth) {
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
                val color = material.lighting(light, point, eye, normal, false, sphere)
                canvas[x + midHeight, y + midWidth] = color
            }
        }
    }

    val path = Paths.get("./chapter6.ppm")

    Files.newBufferedWriter(path).use {
        it.write(canvas.toPpm())
    }
}
