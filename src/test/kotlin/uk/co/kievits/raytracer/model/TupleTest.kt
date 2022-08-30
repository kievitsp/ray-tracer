package uk.co.kievits.raytracer.model

import io.cucumber.junit.platform.engine.Constants
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import org.junit.jupiter.api.Test
import kotlin.math.sqrt
import org.junit.platform.suite.api.ConfigurationParameter

import org.junit.platform.suite.api.IncludeEngines

import org.junit.platform.suite.api.SelectClasspathResource

import org.junit.platform.suite.api.Suite


@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("io/cucumber/skeleton")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "io.cucumber.skeleton")
class TupleTest {
    //    Feature: Tuples, Vectors, and Points

    @Test
    fun `A tuple with w=1 is a point`() {
        val a = Tuple(4.3, -4.2, 3.1, 1.0)
        assert(a.x == 4.3)
        assert(a.y == -4.2)
        assert(a.z == 3.1)
        assert(a.w == 1.0)

        assert(a.isPoint)
        assert(!a.isVector)


    }

    @Test
    fun `A tuple with w=0 is a vector`() {
        val a = Tuple(4.3, -4.2, 3.1, 0.0)
        assert(a.x == 4.3)
        assert(a.y == -4.2)
        assert(a.z == 3.1)
        assert(a.w == 0.0)

        assert(!a.isPoint)
        assert(a.isVector)
    }

    @Test
    fun `Point() creates tuples with w=1`() {
        val p = Point(4, -4, 3)
        assert(p == Tuple(4, -4, 3, 1))

    }

    @Test
    fun `Vector() creates tuples with w=0`() {
        val v = Vector(4, -4, 3)
        assert(v == Tuple(4, -4, 3, 0))

    }

    @Test
    fun `Adding two tuples`() {
        val a1 = Tuple(3, -2, 5, 1)
        val a2 = Tuple(-2, 3, 1, 0)
        assert(a1 + a2 == Tuple(1, 1, 6, 1))

    }

    @Test
    fun `Subtracting two points`() {
        val p1 = Point(3, 2, 1)
        val p2 = Point(5, 6, 7)
        assert(p1 - p2 == Vector(-2, -4, -6))

    }

    @Test
    fun `Subtracting a vector from a point`() {
        val p = Point(3, 2, 1)
        val v = Vector(5, 6, 7)
        assert(p - v == Point(-2, -4, -6))

    }

    @Test
    fun `Subtracting two vectors`() {
        val v1 = Vector(3, 2, 1)
        val v2 = Vector(5, 6, 7)
        assert(v1 - v2 == Vector(-2, -4, -6))

    }

    @Test
    fun `Subtracting a vector from the zero vector`() {
        val zero = Vector(0, 0, 0)
        val v = Vector(1, -2, 3)
        assert(zero - v == Vector(-1, 2, -3))

    }

    @Test
    fun `Negating a tuple`() {
        val a = Tuple(1, -2, 3, -4)
        assert(-a == Tuple(-1, 2, -3, 4))

    }

    @Test
    fun `Multiplying a tuple by a scalar`() {
        val a = Tuple(1, -2, 3, -4)
        assert(a * 3.5 == Tuple(3.5, -7, 10.5, -14))

    }

    @Test
    fun `Multiplying a tuple by a fraction`() {
        val a = Tuple(1, -2, 3, -4)
        assert(a * 0.5 == Tuple(0.5, -1, 1.5, -2))

    }

    @Test
    fun `Dividing a tuple by a scalar`() {
        val a = Tuple(1, -2, 3, -4)
        assert(a / 2.0 == Tuple(0.5, -1, 1.5, -2))

    }

    @Test
    fun `Computing the magnitude of Vector(1, 0, 0)`() {
        val v = Vector(1, 0, 0)
        assert(v.magnitude == 1.0)

    }

    @Test
    fun `Computing the magnitude of Vector(0, 1, 0)`() {
        val v = Vector(0, 1, 0)
        assert(v.magnitude == 1.0)

    }

    @Test
    fun `Computing the magnitude of Vector(0, 0, 1)`() {
        val v = Vector(0, 0, 1)
        assert(v.magnitude == 1.0)

    }

    @Test
    fun `Computing the magnitude of Vector(1, 2, 3)`() {
        val v = Vector(1, 2, 3)
        assert(v.magnitude == sqrt(14.0))

    }

    @Test
    fun `Computing the magnitude of Vector(-1, -2, -3)`() {
        val v = Vector(-1, -2, -3)
        assert(v.magnitude == sqrt(14.0))

    }

    @Test
    fun `Normalizing Vector(4, 0, 0) gives (1, 0, 0)`() {
        val v = Vector(4, 0, 0)
        assert(v.normalise == Vector(1, 0, 0))

    }

    @Test
    fun `Normalizing Vector(1, 2, 3)`() {
        val v = Vector(1, 2, 3)
        // Vector(1 / sqrt(14.0), 2 / sqrt(14.0), 3 / sqrt(14.0))
        assert(v.normalise approx Vector(0.26726, 0.53452, 0.80178))

    }

    @Test
    fun `The magnitude of a normalized vector`() {
        val v = Vector(1, 2, 3)
        val norm = v.normalise
        assert(norm.magnitude == 1.0)

    }

    @Test
    fun `The dot product of two tuples`() {
        val a = Vector(1, 2, 3)
        val b = Vector(2, 3, 4)
        assert(a dot b == 20.0)

    }

    @Test
    fun `The cross product of two vectors`() {
        val a = Vector(1, 2, 3)
        val b = Vector(2, 3, 4)
        assert(a cross b == Vector(-1, 2, -1))
        assert(b cross a == Vector(1, -2, 1))

    }

    @Test fun  `Colors are (red, green, blue) tuples`() {
        val c = Color(-0.5, 0.4, 1.7)
        assert(c.red == -0.5)
        assert(c.green == 0.4)
        assert(c.blue == 1.7)

    }

    @Test fun  `Adding colors`() {
        val c1 = Color(0.9, 0.6, 0.75)
        val c2 = Color(0.7, 0.1, 0.25)
        assert(c1 + c2 == Color(1.6, 0.7, 1.0))

    }

    @Test fun  `Subtracting colors`() {
        val c1 = Color(0.9, 0.6, 0.75)
        val c2 = Color(0.7, 0.1, 0.25)
        assert(c1 - c2 approx Color(0.2, 0.5, 0.5))

    }

    @Test fun  `Multiplying a color by a scalar`() {
        val c = Color(0.2, 0.3, 0.4)
        assert(c * 2.0 == Color(0.4, 0.6, 0.8))

    }

    @Test fun  `Multiplying colors`() {
        val c1 = Color(1, 0.2, 0.4)
        val c2 = Color(0.9, 1, 0.1)

        assert(c1 * c2 approx Color(0.9, 0.2, 0.04))
    }

//
//    @Test fun  `Reflecting a vector approaching at 45°`() {
//        val v = Vector(1, -1, 0)
//        val n = Vector(0, 1, 0)
//        val r = reflect(v, n)
//        assert(r == Vector(1, 1, 0))
//
//    }
//
//    @Test fun  `Reflecting a vector off a slanted surface`() {
//        val v = Vector(0, -1, 0)
//        val n = Vector(sqrt(2.0)/2, sqrt(2.0)/2, 0)
//        val r = reflect(v, n)
//        assert(r == Vector(1, 0, 0))
//    }

}