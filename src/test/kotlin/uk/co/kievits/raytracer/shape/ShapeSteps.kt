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
            "(tuple|vector|point|color)\\(${numberPattern}\\)|(zero|norm|origin|direction|n|position|intensity|point|eyev|normalv|result|c|p)",
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

        ParameterType("comps", "comps") { name -> SharedVars.get<PartialResults>(name) }

        Given("{} ← ray\\({tuple}, {tuple})") { name: String, origin: TUPLE, direction: TUPLE ->
            SharedVars[name] = Ray(origin, direction)
        }

        Given("{} ← {intersections}") { name: String, intersections: Intersections ->
            SharedVars[name] = intersections
        }

        Given("{variable} ← {material}") { name: String, material: Material ->
            SharedVars[name] = material
        }

        Given("{material}.ambient ← {float}") { material: Material, ambient: Float ->
            material.ambient = ambient
        }

        Given("{variable} ← the first object in {world}") { name: String, w: World ->
            SharedVars[name] = w.shapes.first()
        }

        Given("{variable} ← the second object in {world}") { name: String, w: World ->
            SharedVars[name] = w.shapes.first { it != w.shapes.first() }
        }

        Given("{sphere} is added to {world}") { sphere: Sphere, world: World ->
            world.shapes.add(sphere)
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

        When("{variable} ← shade_hit\\({world}, {comps})") { variable: String, world: World, comps: PartialResults ->
            SharedVars[variable] = world.shadeHit(comps)
        }

        When("{variable} ← color_at\\({world}, {ray})") { variable: String, world: World, ray: Ray ->
            SharedVars[variable] = world.colorAt(ray)
        }

        When("{variable} ← point_light\\({tuple}, {tuple})") { name: String, point: POINT, color: COLOR ->
            SharedVars[name] = PointLight(
                intensity = color,
                position = point
            )
        }

        When("{sphere}.material ← {material}") { sphere: Sphere, material: Material ->
            sphere.material = material
        }

        When("{variable} ← lighting\\({material}, {light}, {tuple}, {tuple}, {tuple}\\)") { name: String, material: Material, light: PointLight, position: POINT, eyeV: VECTOR, normalV: VECTOR ->
            SharedVars[name] = material.lighting(light, position, eyeV, normalV, false)
        }

        When("{variable} ← lighting\\({material}, {light}, {tuple}, {tuple}, {tuple}, {boolean}\\)") { name: String, material: Material, light: PointLight, position: POINT, eyeV: VECTOR, normalV: VECTOR, inShadow: Boolean ->
            SharedVars[name] = material.lighting(light, position, eyeV, normalV, inShadow)
        }

        When("{} ← {sphere}.material") { name: String, sphere: Sphere ->
            SharedVars[name] = sphere.material
        }

        When("xs ← intersect_world\\({world}, {ray})") { world: World, ray: Ray ->
            SharedVars["xs"] = world.intersections(ray)
        }

        When("{variable} ← prepare_computations\\({intersection}, {ray})") { name: String, i: Intersection, r: Ray ->
            SharedVars[name] = i.precompute(r)
        }

        When("{world}.light ← point_light\\({tuple}, {tuple})") { world: World, point: POINT, color: COLOR ->
            world.light = PointLight(position = point, intensity = color)
        }

        When("{sphere}.material.ambient ← {float}") { sphere: Sphere, ambient: Float ->
            sphere.material.ambient = ambient
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

        Then("{material} = {material}") { actual: Material, exp: Material ->
            assert(actual == exp)
        }

        Then("{world} contains no objects") { w: World ->
            assert(w.shapes.isEmpty())
        }

        Then("{world} has no light source") { w: World ->
            assert(w.light == null)
        }

        Then("{world}.light = {light}") { w: World, light: PointLight ->
            assert(w.light == light)
        }

        Then("{world} contains {variable}") { w: World, sphere: String ->
            assert(w.shapes.contains(SharedVars[sphere]))
        }

        Then("{comps}.t = {intersection}.t") { comps: PartialResults, i: Intersection -> assert(comps.t == i.t) }
        Then("{comps}.object = {intersection}.object") { comps: PartialResults, i: Intersection -> assert(comps.shape == i.shape) }
        Then("{comps}.point = {tuple}") { comps: PartialResults, point: POINT -> assert(comps.point == point) }
        Then("{comps}.eyev = {tuple}") { comps: PartialResults, eyev: VECTOR -> assert(comps.eyeV == eyev) }
        Then("{comps}.normalv = {tuple}") { comps: PartialResults, normalv: VECTOR -> assert(comps.normalV == normalv) }
        Then("{comps}.inside = {boolean}") { comps: PartialResults, isInside: Boolean -> assert(comps.isInside == isInside) }
        Then("{comps}.over_point.z < {number}") { comps: PartialResults, exp: Float -> assert(comps.overPoint.z == exp) }
        Then("{comps}.point.z > {comps}.over_point.z") { a: PartialResults, b: PartialResults ->
            assert(a.point.z > b.overPoint.z)
        }

        Then("{tuple} = {sphere}.material.color") { color: COLOR, sphere: Sphere ->
            assert(color == sphere.material.color)
        }

        Then("is_shadowed\\({world}, {tuple}) is {boolean}") { w: World, p: POINT, shadow: Boolean ->
            assert(w.isShadowed(p) == shadow)
        }
    }
}
