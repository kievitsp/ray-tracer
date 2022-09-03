package uk.co.kievits.raytracer.shape

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.cucumber.SharedVars.numberPattern
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.model.Sphere
import uk.co.kievits.raytracer.world.World

class ShapeSteps : En {
    init {
        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\(${numberPattern}\\)|(zero|norm|origin|direction|n|position|intensity|point|eyev|normalv|result)",
        ) { type: String?, args: String?, name: String? ->
            SharedVars.buildTuple(args, type, name)
        }

        ParameterType(
            "mVar",
            "(m\\w*|t|IDENTITY_MATRIX|identity_matrix|)|" +
                "(translation|scaling|shearing|rotation_[xyz])\\(${numberPattern}\\)"
        ) { name, function, args ->
            SharedVars.buildMatrix(name, function, args)
        }

        ParameterType(
            "ray",
            "r\\d?",
        ) { name ->
            SharedVars.get<Ray>(name)
        }

        ParameterType(
            "light",
            "light",
        ) { name ->
            SharedVars.get<PointLight>(name)
        }

        ParameterType(
            "material",
            "m|material\\(\\)",
        ) { name ->
            when (name) {
                "material()" -> Material()
                else -> SharedVars.get<Material>(name)
            }
        }

        ParameterType(
            "intersection",
            "(i\\d?)|xs\\[(\\d)\\]"
        ) { value, id ->
            when (id) {
                null -> SharedVars.get<Intersection>(value)
                else -> SharedVars.get<Intersections>("xs")[id.toInt()]
            }
        }

        ParameterType(
            "intersections",
            "intersections\\((.*?)\\)|xs"
        ) { values ->
            when (values) {
                null -> SharedVars["xs"]
                else -> {
                    val intersections = values.split(',').map { SharedVars.get<Intersection>(it.trim()) }
                    when {
                        intersections.isEmpty() -> Intersections.Miss
                        else -> Intersections.Hits(intersections)
                    }
                }
            }
        }

        ParameterType(
            "world",
            "w|world\\(\\)",
        ) { name ->
            when (name) {
                "world()" -> World()
                else -> SharedVars.get<World>(name)
            }
        }

        Given("{} ← ray\\({tuple}, {tuple})") { name: String, origin: TUPLE, direction: TUPLE ->
            SharedVars[name] = Ray(origin, direction)
        }

        Given("{} ← {intersections}") { name: String, intersections: Intersections ->
            SharedVars.vars[name] = intersections
        }

        Given("{} ← {world}") { name: String, world: World ->
            SharedVars.vars[name] = world
        }

        Given("{variable} ← {material}") { name: String, material: Material ->
            SharedVars.vars[name] = material
        }

        Given("{material}.ambient ← {float}") { material: Material, ambient: Float ->
            material.ambient = ambient
        }

        When("{} ← intersection\\({float}, {sphere})") { name: String, t: Float, sphere: Sphere ->
            SharedVars[name] = Intersection(t, sphere)
        }

        When("{} ← transform\\({ray}, {mVar})") { name: String, ray: Ray, m: MATRIX ->
            SharedVars[name] = ray.transform(m)
        }

        When("{} ← normal_at\\({sphere}, {tuple})") { name: String, sphere: Sphere, point: POINT ->
            SharedVars[name] = sphere.normalAt(point)
        }

        When("{} ← hit\\({intersections}\\)") { name: String, intersections: Intersections ->
            SharedVars[name] = intersections.hit()
        }

        When("{} ← point_light\\({tuple}, {tuple})") { name: String, point: POINT, color: COLOR ->
            SharedVars[name] = PointLight(
                intensity = color,
                position = point
            )
        }

        When("{sphere}.material ← {material}") { sphere: Sphere, material: Material ->
            sphere.material = material
        }

        When("{variable} ← lighting\\({material}, {light}, {tuple}, {tuple}, {tuple}\\)") { name: String, material: Material, light: PointLight, position: POINT, eyeV: VECTOR, normalV: VECTOR ->
            SharedVars[name] = material.lighting(light, position, eyeV, normalV)
        }

        When("{} ← {sphere}.material") { name: String, sphere: Sphere ->
            SharedVars[name] = sphere.material
        }

        Then("{ray}.origin = {tuple}") { ray: Ray, exp: TUPLE ->
            assert(ray.origin == exp)
        }

        Then("{ray}.direction = {tuple}") { ray: Ray, exp: TUPLE ->
            assert(ray.direction == exp)
        }

        Then("position\\({ray}, {float}) = {tuple}") { ray: Ray, point: Float, exp: TUPLE ->
            assert(ray.position(point) == exp)
        }

        Then("{intersection} = {intersection}") { actual: Intersection, exp: Intersection ->
            assert(actual == exp)
        }

        Then("{tuple} = normalize\\({tuple})") { actual: TUPLE, exp: TUPLE ->
            assert(actual == exp.normalise)
        }

        Then("{light}.position = {tuple}") { light: PointLight, exp: TUPLE ->
            assert(light.position == exp)
        }

        Then("{light}.intensity = {tuple}") { light: PointLight, exp: TUPLE ->
            assert(light.intensity == exp)
        }

        Then("{material}.color = {tuple}") { material: Material, color: COLOR -> assert(material.color == color) }
        Then("{material}.ambient = {float}") { material: Material, ambient: Float -> assert(material.ambient == ambient) }
        Then("{material}.diffuse = {float}") { material: Material, diffuse: Float -> assert(material.diffuse == diffuse) }
        Then("{material}.specular = {float}") { material: Material, specular: Float -> assert(material.specular == specular) }
        Then("{material}.shininess = {float}") { material: Material, shininess: Float -> assert(material.shininess == shininess) }
//        Then("{material}.reflective = {float}") { material: Material, reflective: Float -> assert(material.reflective == reflective) }

        Then("{material} = {material}") { actual: Material, exp: Material ->
            assert(actual == exp)
        }

        Then("{world} contains no objects") { w: World ->
            assert(w.objects.isEmpty())
        }
        Then("{world} has no light source") { w: World ->
            assert(w.lights.isEmpty())
        }
    }
}
