package day07

import Point
import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): Pair<Point, List<Point>> {
        val start = input.indices.flatMap { row -> input[0].indices.map { col -> row to col } }
            .first { input[it.first][it.second] == 'S' }
        val splitters = input.indices.flatMap { row -> input[0].indices.map { col -> row to col } }
            .filter { input[it.first][it.second] == '^' }

        return start to splitters
    }

    fun getNeighbors(position: Point, splitters: List<Point>): Set<Point> {
        val below = position.first + 1 to position.second
        return if (below in splitters) {
            setOf(below.first to below.second - 1, below.first to below.second + 1)
        } else {
            setOf(below)
        }
    }

    fun part1(input: List<String>): Int {
        val (start, splitters) = parseInput(input)
        var currentBeams = setOf(start)
        var splits = 0

        repeat(input.size) {
            val newBeams = currentBeams.map { getNeighbors(it, splitters) }
            splits += newBeams.count { it.size > 1 }
            currentBeams = newBeams.flatten().toSet()
        }

        return splits
    }


    fun part2(input: List<String>): Long {
        val (start, splitters) = parseInput(input)
        val queue = ArrayDeque(listOf(start))
        val memoization = mutableMapOf<Point, Long>()
        var paths = 0L

        while (queue.isNotEmpty()) {
            val current = queue.removeLast()

            if (current in memoization) {
                paths += memoization[current]!!
                continue
            }
            if (current.first == input.size - 1) {
                paths++
                memoization[current] = 1L
                continue
            }

            val neighbors = getNeighbors(current, splitters)
            if (neighbors.all { it in memoization }) {
                memoization[current] = neighbors.sumOf { memoization[it]!! }
                paths += memoization[current]!!
            } else {
                queue.addAll(neighbors)
            }
        }

        return paths
    }

    val testInput = readInput("day07/day07_test")
    check(part1(testInput), 21)
    check(part2(testInput), 40)

    val input = readInput("day07/day07")
    part1(input).println()
    part2(input).println()
}
