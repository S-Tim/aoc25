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

    fun getNeighbors(position: Pair<Long, Long>, border: Set<Pair<Long, Long>>): Set<Pair<Long, Long>> {
        val directions = (-1..1).flatMap { row -> (-1..1).map { col -> row to col } }

        return directions.map { it.first + position.first to it.second + position.second }
            .filter { it !in border && it != position }.toSet()
    }

    fun flood(start: Pair<Long, Long>, border: Set<Pair<Long, Long>>): Set<Pair<Long, Long>> {
        println("Starting flood fill at $start")
        val visited = mutableSetOf<Pair<Long, Long>>()
        val queue = ArrayDeque<Pair<Long, Long>>(listOf(start))

        while (queue.isNotEmpty()) {
            if(visited.size % 100_000 == 0) {
                println("Flood fill visited size: ${visited.size}, queue size: ${queue.size}")
            }
            val current = queue.removeLast()
            if (current in visited) {
                continue
            }
            visited.add(current)
            queue.addAll(getNeighbors(current, border))
        }

        println("Flood fill done")
        return visited
    }

    fun part2(input: List<String>): Long {
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

//        printAsGrid(border.map { it.first.toInt() to it.second.toInt() }, 2, printRowNumbers = true)


        // Flood Polygon
//        val floodFillStart = Pair(9L, 2L)
        val leftMost = border.minBy { it.second }
        val topMost = border.minBy { it.first }

        val floodFillStart = Pair(topMost.first + 1, leftMost.second + 1)
        val polygonAreaWithoutBorder = flood(floodFillStart, border)
        val polygonArea = polygonAreaWithoutBorder + border
//        printAsGrid(polygonArea.map { it.first.toInt() to it.second.toInt() }, 2)

        var maxArea = 0L
        println("Red Tile size ${redTiles.size}")
        redTiles.indices.flatMap { i ->
            (i + 1 until redTiles.size).mapNotNull { j ->
                val c1 = redTiles[i]
                val c2 = redTiles[j]

                val topLeft = min(c1.first, c2.first) to min(c1.second, c2.second)
                val topRight = min(c1.first, c2.first) to max(c1.second, c2.second)
                val bottomLeft = max(c1.first, c2.first) to min(c1.second, c2.second)
                val bottomRight = max(c1.first, c2.first) to max(c1.second, c2.second)

                val rectangleBorder = mutableSetOf<Pair<Long, Long>>()
                (topLeft.second..topRight.second).forEach { rectangleBorder.add(Pair(topLeft.first, it)) }
                (bottomLeft.second..bottomRight.second).forEach { rectangleBorder.add(Pair(bottomLeft.first, it)) }

                (topLeft.first..bottomLeft.first).forEach { rectangleBorder.add(Pair(it, topLeft.second)) }
                (topRight.first..bottomRight.first).forEach { rectangleBorder.add(Pair(it, topRight.second)) }

                println("Checking rectangle with border size ${rectangleBorder.size}")
                if (rectangleBorder.all { it in polygonArea }) {
                    val dr = c1.first - c2.first
                    val dc = c1.second - c2.second

                    val area = (abs(dr) + 1) * (abs(dc) + 1)
                    if (area > maxArea) {
                        maxArea = area
                        println("New max area: $maxArea for corners $c1 and $c2")
                    }
                }
            }
        }

        return maxArea
    }

    val testInput = readInput("day09/day09_test")
    check(part1(testInput), 50)
//    check(part2(testInput), 24)

    val input = readInput("day09/day09")
    part1(input).println()
    part2(input).println()
}
