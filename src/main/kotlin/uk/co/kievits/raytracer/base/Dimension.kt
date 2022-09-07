package uk.co.kievits.raytracer.base

import jdk.incubator.vector.FloatVector
import jdk.incubator.vector.VectorMask
import jdk.incubator.vector.VectorShuffle
import jdk.incubator.vector.VectorSpecies

typealias D2 = Dimension.D2
typealias D3 = Dimension.D3
typealias D4 = Dimension.D4

sealed class Dimension(
    val size: Int,
    val width: Int,
    val species: VectorSpecies<Float>,
    val sub: Dimension?,
) {

    internal val transposeShuffle = VectorShuffle.fromArray(
        species,
        IntArray(species.length()) { id ->
            val (row, column) = rowAndColumn(id)
            val index = index(row = column, column = row)
            when (index) {
                in 0 until size -> index
                else -> 0
            }
        },
        0
    )

    internal val rowShuffles = (0 until width).map { row ->
        val array = IntArray(species.length()) { column ->
            when {
                column < width -> index(row, column)
                else -> 0
            }
        }
        VectorShuffle.fromArray(species, array, 0)
    }

    internal val columnShuffles = (0 until width).map { column ->
        val array = IntArray(species.length()) { row ->
            when {
                row < width -> index(row, column)
                else -> 0
            }
        }
        VectorShuffle.fromArray(species, array, 0)
    }

    internal val subArrayShuffle: List<List<VectorShuffle<Float>>> = when (sub) {
        null -> emptyList()
        else -> {
            (0 until width)
                .map { skipRow ->
                    (0 until width)
                        .mapNotNull { skipColumn ->
                            val mapping = buildSubShuffle(sub, skipRow, skipColumn)
                            VectorShuffle.fromArray(species, mapping, 0)
                        }
                }
        }
    }

    internal val mask = VectorMask.fromArray(species, BooleanArray(species.length()) { it < size }, 0)
    internal val subMask = sub?.let { sub ->
        VectorMask.fromArray(species, BooleanArray(species.length()) { it < sub.size }, 0)
    }

    private fun buildSubShuffle(
        sub: Dimension,
        skipRow: Int,
        skipColumn: Int
    ): IntArray {
        val rows = (0 until sub.width).map { if (it >= skipRow) it + 1 else it }
        val columns = (0 until sub.width).map { if (it >= skipColumn) it + 1 else it }

        return IntArray(species.length()) {
            when {
                it < sub.size -> {
                    val (newRow, newColumn) = sub.rowAndColumn(it)
                    val oldRow = rows[newRow]
                    val oldColumn = columns[newColumn]
                    index(oldRow, oldColumn)
                }

                else -> 0
            }
        }
    }

    fun index(row: Int, column: Int) = row * width + column
    fun rowAndColumn(id: Int): Pair<Int, Int> {
        val row = id / width
        val column = id % width

        return row to column
    }

    object D2 : Dimension(4, 2, FloatVector.SPECIES_128, null)

    object D3 : Dimension(9, 3, FloatVector.SPECIES_512, D2)

    object D4 : Dimension(16, 4, FloatVector.SPECIES_512, D3)
}
