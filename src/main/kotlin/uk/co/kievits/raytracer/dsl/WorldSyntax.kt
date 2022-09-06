package uk.co.kievits.raytracer.dsl

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.V
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.material.Pattern
import uk.co.kievits.raytracer.shape.Sphere
import uk.co.kievits.raytracer.shape.Plane
import uk.co.kievits.raytracer.shape.Shape
import uk.co.kievits.raytracer.world.World

typealias Syntax<T> = T.() -> Unit

inline fun world(init: Syntax<WorldSyntax> = {}): World {
    val world = World()
    WorldSyntax(world).init()
    return world
}

class WorldSyntax(
    @PublishedApi
    internal val world: World,
) {

    init {
        world.lights = mutableListOf(
            PointLight(Point(-10, 10, -10), Color(1, 1, 1)),
        )
    }

    inline fun sphere(
        init: Syntax<SphereSyntax> = {}
    ): Sphere = Sphere().apply {
        SphereSyntax(this).init()
        world.shapes.add(this)
    }

    inline fun plane(
        init: Syntax<PlaneSyntax> = {}
    ): Plane = Plane().apply {
        PlaneSyntax(this).init()
        world.shapes.add(this)
    }
}

class SphereSyntax(
    override val shape: Sphere = Sphere()
) : ShapeSyntax<Sphere>() {

}

class PlaneSyntax(
    override val shape: Plane = Plane()
) : ShapeSyntax<Plane>() {

}

abstract class ShapeSyntax<S : Shape> {
    abstract protected val shape: S
    var material: Material
        get() = shape.material
        set(value) {
            shape.material = value
        }

    var transform: Matrix<D4>
        get() = shape.transform
        set(value) {
            shape.transform = value
        }

    inline fun material(
        init: Syntax<MaterialSyntax>
    ): Material {
        MaterialSyntax(material).init()
        return material
    }
}

@JvmInline
value class MaterialSyntax(
    private val material: Material,
) {
    var color: COLOR
        get() = material.color
        set(value) {
            material.color = value
        }

    var pattern: Pattern
        get() = material.pattern
        set(value) {
            material.pattern = value
        }

    var ambient: V
        get() = material.ambient
        set(value) {
            material.ambient = value
        }

    var diffuse: V
        get() = material.diffuse
        set(value) {
            material.diffuse = value
        }

    var specular: V
        get() = material.specular
        set(value) {
            material.specular = value
        }

    var shininess: V
        get() = material.shininess
        set(value) {
            material.shininess = value
        }

    var reflective: V
        get() = material.reflective
        set(value) {
            material.reflective = value
        }
}