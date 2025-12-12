package day08

import Point3d
import check
import minus
import println
import readInput
import kotlin.math.sqrt
import kotlin.time.measureTime

fun main() {
    fun parseInput(input: List<String>): List<Point3d> {
        return input.map { it.split(",") }.map { (x, y, z) -> Point3d(x.toInt(), y.toInt(), z.toInt()) }
    }

    fun calculateDistances(junctionBoxes: List<Point3d>): List<Pair<Pair<Point3d, Point3d>, Double>> {
        return junctionBoxes.indices.flatMap { i ->
            (i + 1 until junctionBoxes.size).map { j ->
                val a = junctionBoxes[i]
                val b = junctionBoxes[j]
                val (dx, dy, dz) = a - b
                val distance = sqrt(dx.toDouble() * dx + dy.toDouble() * dy + dz.toDouble() * dz)
                Pair(a, b) to distance
            }
        }.sortedBy { it.second }
    }

    fun calculateCircuit(start: Point3d, connections: Map<Point3d, List<Point3d>>): Set<Point3d> {
        val visited = mutableSetOf<Point3d>()
        val queue = ArrayDeque(listOf(start))

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (current !in visited) {
                visited.add(current)
                val neighbors = connections.getOrDefault(current, emptyList())
                queue.addAll(neighbors)
            }
        }

        return visited
    }

    fun part1(input: List<String>, connectionsToMake: Int = 1000): Int {
        val junctionBoxes = parseInput(input)
        val distances = calculateDistances(junctionBoxes)

        val connections = mutableMapOf<Point3d, List<Point3d>>()
        for (i in 0 until connectionsToMake) {
            val (junctionBoxes, _) = distances[i]
            val (a, b) = junctionBoxes
            connections[a] = connections.getOrDefault(a, emptyList()) + b
            connections[b] = connections.getOrDefault(b, emptyList()) + a
        }

        val circuits = mutableListOf<Set<Point3d>>()
        for (junctionBox in junctionBoxes) {
            if (circuits.all { !it.contains(junctionBox) }) {
                val circuit = calculateCircuit(junctionBox, connections)
                circuits.add(circuit)
            }
        }

        return circuits.sortedByDescending { it.size }.take(3).fold(1) { acc, next -> acc * next.size }
    }

    fun part2(input: List<String>): Long {
        val junctionBoxes = parseInput(input)
        val distances = calculateDistances(junctionBoxes)

        val connections = mutableMapOf<Point3d, List<Point3d>>()
        var connectionsMade = 0
        var lastConnection: Pair<Point3d, Point3d> = junctionBoxes[0] to junctionBoxes[1]

        val circuits = mutableListOf<Set<Point3d>>()
        while (circuits.size != 1) {
            circuits.clear()

            val (boxes, _) = distances[connectionsMade]
            val (a, b) = boxes
            connections[a] = connections.getOrDefault(a, emptyList()) + b
            connections[b] = connections.getOrDefault(b, emptyList()) + a
            lastConnection = a to b
            connectionsMade++

            for (junctionBox in junctionBoxes) {
                if (circuits.all { !it.contains(junctionBox) }) {
                    val circuit = calculateCircuit(junctionBox, connections)
                    circuits.add(circuit)
                }
            }
        }

        return lastConnection.first.first.toLong() * lastConnection.second.first.toLong()
    }

    fun part2BinarySearch(input: List<String>): Long {
        val junctionBoxes = parseInput(input)
        val distances = calculateDistances(junctionBoxes)

        var min = 0
        var max = distances.size - 1

        val circuits = mutableListOf<Set<Point3d>>()
        while (max - min > 1) {
            if (circuits.size == 1) {
                max = (max - min) / 2 + min
            } else if (circuits.size > 1) {
                min = (max - min) / 2 + min
            }
            circuits.clear()

            val connections = mutableMapOf<Point3d, List<Point3d>>()
            for (i in 0 until (max - min) / 2 + min) {
                val (junctionBoxes, _) = distances[i]
                val (a, b) = junctionBoxes
                connections[a] = connections.getOrDefault(a, emptyList()) + b
                connections[b] = connections.getOrDefault(b, emptyList()) + a
            }

            for (junctionBox in junctionBoxes) {
                if (circuits.all { !it.contains(junctionBox) }) {
                    val circuit = calculateCircuit(junctionBox, connections)
                    circuits.add(circuit)
                }
            }
        }

        val (a, b) = distances[min].first
        return a.first.toLong() * b.first.toLong()
    }

    val testInput = readInput("day08/day08_test")
    check(part1(testInput, 10), 40)
    check(part2(testInput), 25272)

    val input = readInput("day08/day08")
    part1(input).println()
    measureTime {  part2(input).println() }.println()
    measureTime {  part2BinarySearch(input).println() }.println()
}
