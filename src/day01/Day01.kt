package day01

import check
import println
import readInput
import kotlin.math.abs


fun main() {

    fun parseInput(input: List<String>): List<Int> {
        return input.map { line ->
            val direction = line.first()
            val distance = line.drop(1).toInt()

            if (direction == 'L') -distance else distance
        }
    }

    fun part1(input: List<String>): Int {
        val instructions = parseInput(input)
        return instructions.runningFold(50) { current, move -> (current + move) % 100 }.count { it == 0 }
    }

    fun part2(input: List<String>): Int {
        val instructions = parseInput(input)
        var currentPosition = 50
        var pointsAtZero = 0

        for (move in instructions) {
            val direction = if (move < 0) -1 else 1
            (0 until abs(move)).forEach {
                currentPosition = (currentPosition + direction) % 100
                if (currentPosition == 0) pointsAtZero++
            }
        }

        return pointsAtZero
    }


    val testInput = readInput("day01/day01_test")
    check(part1(testInput), 3)
    check(part2(testInput), 6)

    val input = readInput("day01/day01")
    part1(input).println()
    part2(input).println()
}
