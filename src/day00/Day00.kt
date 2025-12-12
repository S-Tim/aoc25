package day00

import check
import println
import readInput

fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }


    val testInput = readInput("day00/day00_test")
    check(part1(testInput), 281)
//    check(part2(testInput), 281)

    val input = readInput("day00/day00")
    part1(input).println()
    part2(input).println()
}
