package day03

import check
import println
import readInput
import kotlin.math.max

fun main() {
    fun findMaxJoltage(bank: String, batteries: Int): Long {
        val queue = ArrayDeque<Pair<String, String>>()
        queue.add(Pair(bank, ""))
        var maxJoltage = 0L

        while (!queue.isEmpty()) {
            val (currentBank, currentJoltage) = queue.removeLast()

            if (currentJoltage.length == batteries) {
                maxJoltage = max(maxJoltage, currentJoltage.toLong())
                continue
            }
            if (currentJoltage.isNotEmpty() && maxJoltage.toString().take(currentJoltage.length)
                    .toLong() > currentJoltage.toLong()
            ) {
                continue
            }

            (0..currentBank.length - (batteries - currentJoltage.length)).forEach {
                queue.add(Pair(currentBank.drop(it + 1), currentJoltage + currentBank[it]))
            }
        }

        return maxJoltage
    }

    fun part1(input: List<String>): Long {
        return input.sumOf { findMaxJoltage(it, 2) }
    }

    fun part2(input: List<String>): Long {
        return input.sumOf { findMaxJoltage(it, 12) }
    }


    val testInput = readInput("day03/day03_test")
    check(part1(testInput), 357L)
    check(part2(testInput), 3121910778619L)

    val input = readInput("day03/day03")
    part1(input).println()
    part2(input).println()
}
