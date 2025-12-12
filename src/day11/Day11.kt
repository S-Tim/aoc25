package day11

import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): Map<String, Set<String>> {
        val connections = mutableMapOf<String, Set<String>>()
        for (line in input) {
            val (start, neighbors) = line.split(": ")
            connections[start] = neighbors.split(" ").toSet()
        }
        return connections
    }

    fun findAllPaths(start: String, end: String, connections: Map<String, Set<String>>): Long {
        val queue = ArrayDeque(listOf(start))
        var memoization = mutableMapOf<String, Long>()
        var paths = 0L

        while (queue.isNotEmpty()) {
            val current = queue.removeLast()
            if (current == end) {
                memoization[current] = 1
                paths += 1
                continue
            }

            val neighbors = connections[current] ?: emptySet()
            if (neighbors.all { it in memoization }) {
                val totalPathsFromCurrent = neighbors.sumOf { memoization[it] ?: 0L }
                memoization[current] = totalPathsFromCurrent
                paths += totalPathsFromCurrent
                continue
            } else {
                queue.addAll(neighbors)
            }
        }

        return paths
    }

    fun part1(input: List<String>): Long {
        val connections = parseInput(input)
        return findAllPaths("you", "out", connections)
    }

    fun part2(input: List<String>): Long {
        val connections = parseInput(input)

        val pathsToDac = findAllPaths("svr", "dac", connections)
        val pathsToFft = findAllPaths("svr", "fft", connections)
        val pathsFromDacToFft = findAllPaths("dac", "fft", connections)
        val pathsFromFftToDac = findAllPaths("fft", "dac", connections)
        val pathsFromDacToOut = findAllPaths("dac", "out", connections)
        val pathsFromFftToOut = findAllPaths("fft", "out", connections)

        return (pathsToFft * pathsFromFftToDac * pathsFromDacToOut) + (pathsToDac * pathsFromDacToFft * pathsFromFftToOut)
    }

    val testInput = readInput("day11/day11_test")
    val testInput2 = readInput("day11/day11_2_test")
    check(part1(testInput), 5L)
    check(part2(testInput2), 2L)

    val input = readInput("day11/day11")
    part1(input).println()
    part2(input).println()
}
