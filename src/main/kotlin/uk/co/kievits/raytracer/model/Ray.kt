package uk.co.kievits.raytracer.model

class Ray(
    val origin: POINT,
    val direction: VECTOR,
) {
    fun position(t: V): POINT {
        return origin + (direction * t)
    }

    fun transform(transform: MATRIX): Ray = Ray(
        origin = transform * origin,
        direction = transform * direction,
    )
}