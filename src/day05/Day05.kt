package day05

import check
import println
import readInput
import kotlin.math.max

fun main() {
    fun parseInput(input: List<String>): Pair<List<LongRange>, List<Long>> {
        val (rangeInput, ingredientsInput) = input.joinToString("\n").split("\n\n")

        val ranges = rangeInput.lines().map { it.split("-") }.map { it.first().toLong()..it.last().toLong() }
        val ingredients = ingredientsInput.lines().map { it.toLong() }

        return ranges to ingredients
    }

    fun part1(input: List<String>): Int {
        val (ranges, ingredients) = parseInput(input)

        return ingredients.count { ingredient -> ranges.any { ingredient in it } }
    }

    fun part2(input: List<String>): Long {
        val (ranges, _) = parseInput(input)

        val sorted = ranges.sortedBy { it.first }
        val merged = mutableListOf<LongRange>()

        var current = sorted.first()
        for (i in 1 until sorted.size) {
            val next = sorted[i]
            if (next.first <= current.last) {
                current = current.first..max(current.last, next.last)
            } else {
                merged.add(current)
                current = next
            }
        }
        merged.add(current)

        return merged.sumOf { it.last - it.first + 1 }
    }


    val testInput = readInput("day05/day05_test")
    check(part1(testInput), 3)
    check(part2(testInput), 14L)

    val input = readInput("day05/day05")
    part1(input).println()
    part2(input).println()
}
