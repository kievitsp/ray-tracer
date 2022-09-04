package uk.co.kievits.raytracer.shape

import uk.co.kievits.raytracer.base.V

sealed class Intersections : List<Intersection> {

    abstract operator fun plus(other: Intersections): Intersections

    abstract fun hit(): Intersection?

    object Miss : Intersections(), List<Intersection> by emptyList() {
        override fun plus(
            other: Intersections
        ): Intersections = when (other) {
            is Hits -> other
            else -> this
        }

        override fun hit(): Intersection? = null
    }

    data class Hits private constructor(
        val hits: List<Intersection>
    ) : Intersections(), List<Intersection> by hits {
        override fun plus(
            other: Intersections
        ): Intersections = when (other) {
            is Hits -> invoke(hits + other.hits)
            Miss -> this
            else -> this
        }

        override fun hit(): Intersection? = hits
            .firstOrNull { it.t >= 0f }

        companion object {
            operator fun invoke(
                hits: List<Intersection>,
                isSorted: Boolean = false,
            ): Hits = when (isSorted) {
                true -> Hits(hits)
                else -> Hits(hits.sortedBy { it.t })
            }
        }
    }

    companion object {
        operator fun invoke(
            t: V,
            shape: Shape,
        ): Intersections = Intersections.Hits(
            listOf(Intersection(t, shape))
        )

        operator fun invoke(
            hits: List<Intersection>,
            isSorted: Boolean = false,
        ): Intersections = when (hits.isEmpty()) {
            true -> Miss
            false -> when (isSorted) {
                true -> Hits(hits)
                false -> Hits(hits.sortedBy { it.t })
            }
        }
    }
}
