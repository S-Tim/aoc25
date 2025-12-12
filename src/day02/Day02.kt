package day02

import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): List<LongRange> {
        val ranges = input.first().split(",")
        return ranges.map { it.split("-") }.map { it -> it.first().toLong()..it.last().toLong() }
    }

    fun isValid(id: Long): Boolean {
        val idAsString = id.toString()
        // IDs with odd length cannot have two equal halves
        if (idAsString.length % 2 != 0) return true

        // if the halves are equal, the ID is invalid
        return idAsString.take(idAsString.length / 2) != idAsString.drop(idAsString.length / 2)
    }

    fun isValidPart2(id: Long): Boolean {
        val idAsString = id.toString()

        // Check if the ID is made up of repeated patterns starting from length 1 up to half the length of the ID
        (1..idAsString.length / 2).forEach {
            // If the IDs length is not divisible by the pattern length, skip because it can't be made up of repeated patterns of that length
            if (idAsString.length % it == 0) {
                val pattern = idAsString.take(it)
                if (idAsString.windowed(it, it).all { window -> window == pattern }) {
                    return false
                }
            }
        }
        return true
    }

    fun part1(input: List<String>): Long {
        val ranges = parseInput(input)
        val invalidIds = ranges.flatMap { it.filter { !isValid(it) } }
        return invalidIds.sum()
    }

    fun part2(input: List<String>): Long {
        val ranges = parseInput(input)
        val invalidIds = ranges.flatMap { it.filter { !isValidPart2(it) } }
        return invalidIds.sum()
    }


    val testInput = readInput("day02/day02_test")
    check(part1(testInput), 1227775554)
    check(part2(testInput), 4174379265)

    val input = readInput("day02/day02")
    part1(input).println()
    part2(input).println()
}
