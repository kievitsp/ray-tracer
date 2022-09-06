package uk.co.kievits.raytracer.shape

import io.cucumber.java8.En
import uk.co.kievits.raytracer.base.COLOR
import uk.co.kievits.raytracer.base.D4
import uk.co.kievits.raytracer.base.MATRIX
import uk.co.kievits.raytracer.base.Matrix
import uk.co.kievits.raytracer.base.POINT
import uk.co.kievits.raytracer.base.Ray
import uk.co.kievits.raytracer.base.TUPLE
import uk.co.kievits.raytracer.base.VECTOR
import uk.co.kievits.raytracer.cucumber.SharedVars
import uk.co.kievits.raytracer.cucumber.SharedVars.numberPattern
import uk.co.kievits.raytracer.cucumber.SharedVars.parseFloat
import uk.co.kievits.raytracer.light.PointLight
import uk.co.kievits.raytracer.material.BasePattern
import uk.co.kievits.raytracer.material.CheckeredPattern
import uk.co.kievits.raytracer.material.GradientPattern
import uk.co.kievits.raytracer.material.Material
import uk.co.kievits.raytracer.material.Pattern
import uk.co.kievits.raytracer.material.RingPattern
import uk.co.kievits.raytracer.material.StripedPattern
import uk.co.kievits.raytracer.world.World

class ShapeSteps : En {
    init {
        ParameterType(
            "tuple",
            "(tuple|vector|point|color)\\(${numberPattern}\\)|" +
                "(zero|norm|origin|direction|n|position|intensity|point|eyev|normalv|" +
                "result|c\\d?|p|n\\d|black|white|color)",
        ) { type: String?, args: String?, name: String? ->
            SharedVars.buildTuple(args, type, name)
        }

        ParameterType(
            "matrix",
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
                    val intersections = when {
                        ":" in values -> values.split(",")
                            .map { it.trim() }
                            .map {
                                val split = it.split(":")
                                Intersection(parseFloat(split[0]), SharedVars[split[1]])
                            }

                        else -> values.split(',').map { SharedVars[it.trim()] }
                    }

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

        Given("{} ← {pattern}") { name: String, pattern: Pattern -> SharedVars[name] = pattern }

        Given("{} ← {intersections}") { name: String, intersections: Intersections ->
            SharedVars[name] = intersections
        }

        Given("{variable} ← {material}") { name: String, material: Material -> SharedVars[name] = material }

        Given("{material}.{variable} ← {float}") { material: Material, variable: String, value: Float ->
            when (variable) {
                "ambient" -> material.ambient = value
                "diffuse" -> material.diffuse = value
                "specular" -> material.specular = value
                "shininess" -> material.shininess = value
                "reflective" -> material.reflective = value
                "transparency" -> material.transparency = value
                "refractive_index" -> material.refractiveIndex = value
                else -> TODO(variable)
            }
        }

        Given("{material}.pattern ← stripe_pattern\\({tuple}, {tuple})") { material: Material, a: COLOR, b: COLOR ->
            material.pattern = StripedPattern(a, b)
        }

        Given("{variable} ← the first object in {world}") { name: String, w: World ->
            SharedVars[name] = w.shapes.first()
        }

        Given("{variable} ← the second object in {world}") { name: String, w: World ->
            SharedVars[name] = w.shapes.first { it != w.shapes.first() }
        }

        Given("{variable} is added to {world}") { name: String, world: World ->
            world.shapes.add(SharedVars[name])
        }

        Given("{variable} ← stripe_pattern\\({tuple}, {tuple})") { name: String, first: TUPLE, second: TUPLE ->
            SharedVars[name] = StripedPattern(first, second)
        }

        Given("{variable} ← gradient_pattern\\({tuple}, {tuple})") { name: String, first: TUPLE, second: TUPLE ->
            SharedVars[name] = GradientPattern(first, second)
        }

        Given("{variable} ← ring_pattern\\({tuple}, {tuple})") { name: String, first: TUPLE, second: TUPLE ->
            SharedVars[name] = RingPattern(first, second)
        }

        Given("{variable} ← checkers_pattern\\({tuple}, {tuple})") { name: String, first: TUPLE, second: TUPLE ->
            SharedVars[name] = CheckeredPattern(first, second)
        }

        Given("set_pattern_transform\\({pattern}, {matrix})") { pattern: Pattern, matrix: Matrix<D4> ->
            pattern.transform = matrix
        }

        When("{} ← intersection\\({number}, {shape})") { name: String, t: Float, shape: Shape ->
            SharedVars[name] = Intersection(t, shape)
        }

        When("{} ← transform\\({ray}, {matrix})") { name: String, ray: Ray, m: MATRIX ->
            SharedVars[name] = ray.transform(m)
        }

        When("{variable} ← normal_at\\({shape}, {tuple})") { name: String, shape: Shape, point: POINT ->
            SharedVars[name] = shape.normalAt(point)
        }

        When("{variable} ← local_normal_at\\({shape}, {tuple})") { name: String, shape: Shape, point: POINT ->
            SharedVars[name] = shape.localNormalAt(point)
        }

        When("{} ← hit\\({intersections}\\)") { name: String, intersections: Intersections ->
            SharedVars[name] = intersections.hit()
        }

        When("{variable} ← shade_hit\\({world}, {comps})") { variable: String, world: World, comps: PartialResults ->
            SharedVars[variable] = world.shadeHit(comps)
        }

        When("{variable} ← shade_hit\\({world}, {comps}, {int})") { variable: String, world: World, comps: PartialResults, remaining: Int ->
            SharedVars[variable] = world.shadeHit(comps, remaining)
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

        When("{shape}.material ← {material}") { shape: Shape, material: Material ->
            shape.material = material
        }

        When("{variable} ← lighting\\({material}, {light}, {tuple}, {tuple}, {tuple}\\)") { name: String, material: Material, light: PointLight, position: POINT, eyeV: VECTOR, normalV: VECTOR ->
            SharedVars[name] = material.lighting(light, position, eyeV, normalV, false, Sphere())
        }

        When("{variable} ← lighting\\({material}, {light}, {tuple}, {tuple}, {tuple}, {boolean}\\)") { name: String, material: Material, light: PointLight, position: POINT, eyeV: VECTOR, normalV: VECTOR, inShadow: Boolean ->
            SharedVars[name] = material.lighting(light, position, eyeV, normalV, inShadow, Sphere())
        }

        When("{} ← {shape}.material") { name: String, shape: Shape ->
            SharedVars[name] = shape.material
        }

        When("xs ← intersect_world\\({world}, {ray})") { world: World, ray: Ray ->
            SharedVars["xs"] = world.intersections(ray)
        }

        When("{variable} ← prepare_computations\\({intersection}, {ray})") { name: String, i: Intersection, r: Ray ->
            SharedVars[name] = i.precompute(r, Intersections.Miss)
        }

        When("{variable} ← prepare_computations\\({intersection}, {ray}, {intersections})") { name: String, i: Intersection, r: Ray, xs: Intersections ->
            SharedVars[name] = i.precompute(r, xs)
        }

        When("{world}.light ← point_light\\({tuple}, {tuple})") { world: World, point: POINT, color: COLOR ->
            world.light = PointLight(position = point, intensity = color)
        }

        When("{shape}.material.ambient ← {float}") { shape: Shape, ambient: Float ->
            shape.material.ambient = ambient
        }

        When("{variable} ← stripe_at_object\\({pattern}, {shape}, {tuple})") { name: String, pattern: StripedPattern, shape: Shape, point: POINT ->
            SharedVars[name] = pattern.atShape(shape, point)
        }

        When("{variable} ← pattern_at_shape\\({pattern}, {shape}, {tuple})") { name: String, pattern: Pattern, shape: Shape, point: POINT ->
            SharedVars[name] = pattern.atShape(shape, point)
        }

        When("{variable} ← reflected_color\\({world}, {comps})") { name: String, world: World, comps: PartialResults ->
            SharedVars[name] = world.reflectedColor(comps)
        }

        When("{variable} ← reflected_color\\({world}, {comps}, {int})") { name: String, world: World, comps: PartialResults, limit: Int ->
            SharedVars[name] = world.reflectedColor(comps, limit)
        }

        When("{variable} ← refracted_color\\({world}, {comps}, {int})") { name: String, world: World, comps: PartialResults, limit: Int ->
            SharedVars[name] = world.refractedColor(comps, limit)
        }

        When("{variable} ← schlick\\({comps}\\)") { name: String, comps: PartialResults ->
            SharedVars[name] = comps.schlick()
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

        Then("{material}.{variable} = {float}") { material: Material, variable: String, exp: Float ->
            assertMaterial(material, variable, exp)
        }

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

        Then("{world} contains {variable}") { w: World, shape: String ->
            assert(w.shapes.contains(SharedVars[shape]))
        }

        Then("{comps}.t = {intersection}.t") { comps: PartialResults, i: Intersection -> assert(comps.t == i.t) }
        Then("{comps}.object = {intersection}.object") { comps: PartialResults, i: Intersection -> assert(comps.shape == i.shape) }
        Then("{comps}.point = {tuple}") { comps: PartialResults, point: POINT -> assert(comps.point == point) }
        Then("{comps}.eyev = {tuple}") { comps: PartialResults, eyev: VECTOR -> assert(comps.eyeV == eyev) }
        Then("{comps}.normalv = {tuple}") { comps: PartialResults, normalv: VECTOR -> assert(comps.normalV == normalv) }
        Then("{comps}.inside = {boolean}") { comps: PartialResults, isInside: Boolean -> assert(comps.isInside == isInside) }
        Then("{comps}.reflectv = {tuple}") { comps: PartialResults, reflectv: VECTOR -> assert(comps.reflectV == reflectv) }
        Then("{comps}.over_point.z < {number}") { comps: PartialResults, exp: Float -> assert(comps.overPoint.z < exp) }
        Then("{comps}.under_point.z > {number}") { comps: PartialResults, exp: Float -> assert(comps.underPoint.z > exp) }
        Then("{comps}.point.z > {comps}.over_point.z") { a: PartialResults, b: PartialResults ->
            assert(a.point.z > b.overPoint.z)
        }
        Then("{comps}.point.z < {comps}.under_point.z") { a: PartialResults, b: PartialResults ->
            assert(a.point.z < b.underPoint.z)
        }
        Then("{comps}.n1 = {number}") { comps: PartialResults, exp: Float -> assert(comps.n1 == exp) }
        Then("{comps}.n2 = {number}") { comps: PartialResults, exp: Float -> assert(comps.n2 == exp) }

        Then("{tuple} = {shape}.material.color") { color: COLOR, shape: Shape ->
            assert(color == shape.material.color)
        }

        Then("is_shadowed\\({world}, {tuple}) is {boolean}") { w: World, p: POINT, shadow: Boolean ->
            assert(w.isShadowed(p) == shadow)
        }

        Then("{pattern}.a = {tuple}") { pattern: StripedPattern, color: COLOR -> assert(pattern.first == color) }
        Then("{pattern}.b = {tuple}") { pattern: StripedPattern, color: COLOR -> assert(pattern.second == color) }

        Then("stripe_at\\({pattern}, {tuple}) = {tuple}") { pattern: BasePattern, point: POINT, color: COLOR ->
            assert(pattern.atPattern(point) == color)
        }

        Then("pattern_at\\({pattern}, {tuple}) = {tuple}") { pattern: Pattern, point: POINT, color: COLOR ->
            assert(pattern.atPattern(point) == color)
        }

        Then("color_at\\({world}, {ray}) should terminate successfully") { world: World, ray: Ray ->
            world.colorAt(ray)
        }
    }
}

fun assertMaterial(material: Material, variable: String, exp: Float) {
    when (variable) {
        "ambient" -> assert(material.ambient == exp)
        "diffuse" -> assert(material.diffuse == exp)
        "specular" -> assert(material.specular == exp)
        "shininess" -> assert(material.shininess == exp)
        "reflective" -> assert(material.reflective == exp)
        "transparency" -> assert(material.transparency == exp)
        "refractive_index" -> assert(material.refractiveIndex == exp)
        else -> TODO(variable)
    }
}
