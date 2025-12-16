package day09

import check
import println
import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun parseInput(input: List<String>): List<Pair<Long, Long>> {
        return input.map { it.split(",").map { it.toLong() } }.map { it.first() to it.last() }
    }

    fun part1(input: List<String>): Long {
        val redTiles = parseInput(input)

        val rectangleAreas = redTiles.indices.flatMap { i ->
            (i + 1 until redTiles.size).map { j ->
                val dr = redTiles[i].first - redTiles[j].first
                val dc = redTiles[i].second - redTiles[j].second

                (abs(dr) + 1) * (abs(dc) + 1)
            }
        }

        return rectangleAreas.max()
    }

    fun isInPolygon(point: Pair<Long, Long>, polygonBorder: Set<Pair<Long, Long>>, goRight: Boolean): Boolean {
        if (point in polygonBorder) {
            return true
        }

        if (goRight) {
            return polygonBorder.count { it.first == point.first && it.second >= point.second } % 2 == 1
        } else {
            return polygonBorder.count { it.first == point.first && it.second <= point.second } % 2 == 1
        }

    }

    fun isInPolygon(
        c1: Pair<Long, Long>,
        c2: Pair<Long, Long>,
        polygonBorder: Map<Long, Set<Pair<Long, Long>>>,
        minCol: Long,
        maxCol: Long
    ): Boolean {
        val topLeft = min(c1.first, c2.first) to min(c1.second, c2.second)
        val topRight = min(c1.first, c2.first) to max(c1.second, c2.second)
        val bottomLeft = max(c1.first, c2.first) to min(c1.second, c2.second)
        val bottomRight = max(c1.first, c2.first) to max(c1.second, c2.second)

        val minRow = topLeft.first
        val maxRow = bottomLeft.first
        val maxRectCol = topRight.second
        val minRectCol = topLeft.second

        var goRight = true
        var filtered: Set<Pair<Long, Long>>
        if (abs(minCol - minRectCol) < abs(maxCol - maxRectCol)) {
            goRight = false
            filtered = polygonBorder.filter { it.key in minRow..maxRow }.map { it.value }.flatten().filter { it.second >= minRectCol }.toSet()
        } else {
            filtered = polygonBorder.filter { it.key in minRow..maxRow }.map { it.value }.flatten().filter { it.second <= maxRectCol }.toSet()
        }

        if ((topLeft.second..topRight.second).any {
                !isInPolygon(
                    Pair(topLeft.first, it),
                    filtered,
                    goRight
                )
            }) return false
        if ((bottomLeft.second..bottomRight.second).any {
                !isInPolygon(
                    Pair(bottomLeft.first, it),
                    filtered,
                    goRight
                )
            }) return false
        if ((topLeft.first..bottomLeft.first).any {
                !isInPolygon(
                    Pair(it, topLeft.second),
                    filtered,
                    goRight
                )
            }) return false
        if ((topRight.first..bottomRight.first).any {
                !isInPolygon(
                    Pair(it, topRight.second),
                    filtered,
                    goRight
                )
            }) return false

        return true
    }

    fun part22(input: List<String>): Long {
        val redTiles = parseInput(input)

        // Build polygon outline
        val border = mutableSetOf<Pair<Long, Long>>()
        for (i in redTiles.indices) {
            val current = redTiles[i]
            val next = redTiles[(i + 1) % redTiles.size]
            border.add(current)

            if (current.first == next.first) {
                // same row
                val min = min(current.second, next.second)
                val max = max(current.second, next.second)
                (min until max).forEach {
                    border.add(Pair(current.first, it))
                }
            } else if (current.second == next.second) {
                // same column
                val min = min(current.first, next.first)
                val max = max(current.first, next.first)
                (min until max).forEach {
                    border.add(Pair(it, current.second))
                }
            } else {
                throw IllegalArgumentException("Invalid input")
            }
        }

        val borderByRow = border.groupBy { it.first }.map { (row, points) -> row to points.sortedBy { it.second }.toSet() }.toMap()

        val minCol = border.minBy { it.second }.second
        var maxCol = border.maxBy { it.second }.second

        var maxArea = 0L
        println("Red Tile size ${redTiles.size}")
        val rectanglesToCheck = redTiles.indices.flatMap { i ->
            (i + 1 until redTiles.size).mapNotNull { j -> i to j }
        }.count()
        println("Total rectangles to check: $rectanglesToCheck")

        redTiles.indices.forEach { i ->
            (i + 1 until redTiles.size).forEach { j ->
                val c1 = redTiles[i]
                val c2 = redTiles[j]

                val dr = c1.first - c2.first
                val dc = c1.second - c2.second

                val area = (abs(dr) + 1) * (abs(dc) + 1)

                // 1_561_582_800 -> too high
                if (area > maxArea) {
                    println("Checking rectangle with area ${area}")
                    if (isInPolygon(c1, c2, borderByRow, minCol, maxCol)) {
                        maxArea = area
                        println("New max area: $maxArea for corners $c1 and $c2")
                    }
                } else {
                    println("Skipping because too small")
                }
            }
        }

        return maxArea
    }

    val testInput = readInput("day09/day09_test")
    check(part1(testInput), 50)
//    check(part22(testInput), 24)

    val input = readInput("day09/day09")
    part1(input).println()
    part22(input).println()
}
