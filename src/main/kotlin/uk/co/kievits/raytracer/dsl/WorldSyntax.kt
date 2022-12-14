package uk.co.kievits.raytracer.dsl

import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.Color
import uk.co.kievits.raytracer.base.Colors.BLACK
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.Point
import uk.co.kievits.raytracer.base.toV
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.material.Pattern
import uk.co.kievits.raytracer.shape.Cone
import uk.co.kievits.raytracer.shape.Cube
import uk.co.kievits.raytracer.shape.Cylinder
import uk.co.kievits.raytracer.shape.CylinderLike
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
        init: Syntax<ShapeSyntax> = {}
    ): Sphere = Sphere().apply {
        ShapeSyntax(this).init()
        world.shapes.add(this)
    }

    inline fun plane(
        init: Syntax<ShapeSyntax> = {}
    ): Plane = Plane().apply {
        ShapeSyntax(this).init()
        world.shapes.add(this)
    }

    inline fun cube(
        init: Syntax<ShapeSyntax> = {}
    ): Cube = Cube().apply {
        ShapeSyntax(this).init()
        world.shapes.add(this)
    }

    inline fun cylinder(
        init: Syntax<CylinderSyntax> = {}
    ): Cylinder = Cylinder().apply {
        CylinderSyntax(this).init()
        world.shapes.add(this)
    }

    inline fun cone(
        init: Syntax<CylinderSyntax> = {}
    ): Cone = Cone().apply {
        CylinderSyntax(this).init()
        world.shapes.add(this)
    }
}

open class CylinderSyntax(
    override val shape: CylinderLike,
) : ShapeSyntax(shape) {

    var closed
        get() = shape.closed
        set(value) {
            shape.closed = value
        }
    var minimum: Number
        get() = shape.minimum
        set(value) {
            shape.minimum = value.toV()
        }
    var maximum: Number
        get() = shape.maximum
        set(value) {
            shape.maximum = value.toV()
        }
}

open class ShapeSyntax(
    protected open val shape: Shape
) {
    val glass: Material
        get() = Material(
            color = BLACK,
            transparency = 1.0,
            refractiveIndex = 1.5,
            reflective = 1.0,
            diffuse = .01,
            ambient = .01,
            specular = 1.0,
            shininess = 300.0,
//            shadeRatio = .1f
        )
    val mirror: Material
        get() = Material(
            color = BLACK,
            reflective = 1.0,
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
            material.ambient = value.toV()
        }

    var diffuse: Number
        get() = material.diffuse
        set(value) {
            material.diffuse = value.toV()
        }

    var specular: Number
        get() = material.specular
        set(value) {
            material.specular = value.toV()
        }

    var shininess: Number
        get() = material.shininess
        set(value) {
            material.shininess = value.toV()
        }

    var reflective: Number
        get() = material.reflective
        set(value) {
            material.reflective = value.toV()
        }

    var transparency: Number
        get() = material.transparency
        set(value) {
            material.transparency = value.toV()
        }
    var refractiveIndex: Number
        get() = material.refractiveIndex
        set(value) {
            material.refractiveIndex = value.toV()
        }
}
