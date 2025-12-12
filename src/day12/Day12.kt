package day12

import check
import println
import readInput

fun main() {
    fun isPossible(area: Pair<Int, List<Int>>, presentSizes: List<Int>): Boolean {
        val availableArea = area.first
        val neededArea = area.second.mapIndexed { index, count -> presentSizes[index] * count }.sum()

        return neededArea.toDouble() < availableArea
    }

    fun part1(input: List<String>): Int {
        val blocks = input.joinToString("\n").split("\n\n")
        val presents = blocks.take(blocks.size - 1)
        val areas = blocks.last()

        val presentSizes = presents.map { present -> present.count { it == '#' } }
        val parsedAreas = areas.split("\n").map { area ->
            val (size, presentCounts) = area.split(": ")
            val area = size.split("x").map { it.toInt() }.reduce { a, b -> a * b }
            val presentsAsList = presentCounts.split(" ").map { it.toInt() }
            area to presentsAsList
        }

        val possbible = parsedAreas.count { isPossible(it, presentSizes) }
        val impossible = parsedAreas.size - possbible

        println("Possible areas: $possbible")
        println("Impossible areas: $impossible")

        return input.size
    }

    val testInput = readInput("day12/day12_test")
//    check(part1(testInput), 281)
//    check(part2(testInput), 281)

    val input = readInput("day12/day12")
    part1(input).println()
}
