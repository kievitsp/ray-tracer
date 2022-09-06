package uk.co.kievits.raytracer.dsl

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Colors.BLACK
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.material.Pattern
import uk.co.kievits.raytracer.shape.Plane
import uk.co.kievits.raytracer.shape.Shape
import uk.co.kievits.raytracer.shape.Sphere
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
) : ShapeSyntax<Sphere>()

class PlaneSyntax(
    override val shape: Plane = Plane()
) : ShapeSyntax<Plane>()

abstract class ShapeSyntax<S : Shape> {
    protected abstract val shape: S

    val glass: Material
        get() = Material(
            color = BLACK,
            transparency = 1.0f,
            refractiveIndex = 1.5f,
            reflective = 1.0f,
            diffuse = .01f,
            ambient = .01f,
            specular = 1.0f,
            shininess = 300.0f,
//            shadeRatio = .1f
        )
    val mirror: Material
        get() = Material(
            color = BLACK,
            reflective = 1.0f,
        )

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

    var ambient: Number
        get() = material.ambient
        set(value) {
            material.ambient = value.toFloat()
        }

    var diffuse: Number
        get() = material.diffuse
        set(value) {
            material.diffuse = value.toFloat()
        }

    var specular: Number
        get() = material.specular
        set(value) {
            material.specular = value.toFloat()
        }

    var shininess: Number
        get() = material.shininess
        set(value) {
            material.shininess = value.toFloat()
        }

    var reflective: Number
        get() = material.reflective
        set(value) {
            material.reflective = value.toFloat()
        }

    var transparency: Number
        get() = material.transparency
        set(value) {
            material.transparency = value.toFloat()
        }
    var refractiveIndex: Number
        get() = material.refractiveIndex
        set(value) {
            material.refractiveIndex = value.toFloat()
        }
}
