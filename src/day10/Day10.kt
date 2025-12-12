package day10

import check
import println
import readInput

fun main() {

    fun parseInput(input: List<String>): List<Triple<String, List<List<Int>>, List<Int>>> {
        val machinePattern = """\[([.#]+)]((?: \(\d+(?:,\d+)*\))+) \{(\d+(?:,\d+)*)}""".toRegex()

        val machines = input.mapNotNull { machinePattern.find(it) }.map { it.groupValues.drop(1) }
            .map { (indicators, buttons, joltages) -> Triple(indicators, buttons, joltages) }

        return machines.map { (indicators, buttons, joltages) ->
            val mappedButtons =
                buttons.trim().split(" ").map { it.replace("(", "").replace(")", "").split(",").map { it.toInt() } }
            val mappedJoltages = joltages.split(",").map { it.toInt() }
            Triple(indicators, mappedButtons, mappedJoltages)
        }
    }

    fun pressButton(indicators: String, button: List<Int>): String {
        return indicators.mapIndexed { index, indicator ->
            if (index in button)
                if (indicator == '#') '.' else '#'
            else indicator
        }.joinToString("")
    }

    fun findShortestInitSequence(indicators: String, buttons: List<List<Int>>): Int {
        val start = ".".repeat(indicators.length)
        val queue = ArrayDeque(listOf(start to 0))
        val visited = mutableSetOf<String>()

        while (queue.isNotEmpty()) {
            val (current, buttonPresses) = queue.removeFirst()
            if (current in visited) {
                continue
            }
            visited.add(current)
            if (current == indicators) {
//                println("Found solution with $buttonPresses presses to reach $indicators")
                return buttonPresses
            }
            for (button in buttons) {
                val nextState = pressButton(current, button)
                queue.addLast(nextState to buttonPresses + 1)
            }
        }

        return -1
    }

    fun part1(input: List<String>): Int {
        val machines = parseInput(input)
        return machines.sumOf { findShortestInitSequence(it.first, it.second) }
    }

    fun findShortestJoltageConfiguration(joltages: List<Int>, buttons: List<List<Int>>): Int {
        val start = List(joltages.size) { 0 }
        val queue = ArrayDeque(listOf(start to 0))
        val visited = mutableSetOf<String>()
        var maxDepth = 0

        val optimizedButtons = buttons.map { button ->
            val l = MutableList(joltages.size) { 0 }
            for (index in button) {
                l[index] = 1
            }
            l
        }

        while (queue.isNotEmpty()) {
            val (current, buttonPresses) = queue.removeFirst()
//            if (buttonPresses > 11) {
//                println("Could not find solution with $buttonPresses presses to reach $joltages")
//                return buttonPresses
//            }
//            if(buttonPresses % 10 == 0) {
//                println("At $buttonPresses presses, queue size: ${queue.size}")
//            }
            if (current.joinToString() in visited) {
                continue
            }
            visited.add(current.joinToString())
            if(buttonPresses > maxDepth) {
                maxDepth = buttonPresses
                println("New max depth: $maxDepth, visited size: ${visited.size}, queue size: ${queue.size}")
            }
            if (current == joltages) {
                println("Found solution with $buttonPresses presses to reach $joltages")
                return buttonPresses
            }

//            for (button in buttons) {
//                val next = current.mapIndexed { index, joltage -> if (index in button) joltage + 1 else joltage }
//                if (next.indices.all { next[it] <= joltages[it] }) {
//                    queue.addLast(next to buttonPresses + 1)
//                }
//            }

            for (button in optimizedButtons) {
                val next = current.indices.map { current[it] + button[it] }
                if (next.indices.all { next[it] <= joltages[it] }) {
                    queue.addLast(next to buttonPresses + 1)
                }
            }
        }

        return -1
    }

    fun part2(input: List<String>): Int {
        /**
         * Buttons have an effect e on the joltage:
         * ei = 0 or 1 for each index in joltages
         * example= e1 = [0, 0, 1, 1]
         *
         * Constraints:
         * The total effect of all buttons on each joltage must be equal to the target joltage
         * sum(bi * eij for i in e) = joltage_j
         *
         * bi >= 0
         *
         * Objective:
         * sum(bi) -> min
         */

        val machines = parseInput(input)

//        val map = machines.parallelStream().map { findShortestJoltageConfiguration(it.third, it.second) }
//        return map.toList().sum()

        return machines.sumOf { findShortestJoltageConfiguration(it.third, it.second) }
    }


    val testInput = readInput("day10/day10_test")
    check(part1(testInput), 7)
    check(part2(testInput), 33)

    val input = readInput("day10/day10")
    part1(input).println()
    part2(input).println()
}
