package day06

import check
import println
import readInput

fun main() {
    fun parseInput(input: List<String>): List<List<String>> {
        val lines = input.map { it.split(("\\s+".toRegex())).filter { it.isNotBlank() } }
        val transposed = lines[0].indices.map { col ->
            lines.indices.map { row ->
                lines[row][col]
            }
        }

        return transposed
    }

    fun calculateAnswer(numbers: List<Long>, operator: String): Long {
        return when (operator) {
            "+" -> numbers.sum()
            "*" -> numbers.reduce { acc, i -> acc * i }
            else -> 0
        }
    }

    fun part1(input: List<String>): Long {
        val problems = parseInput(input)
        return problems.sumOf { calculateAnswer(it.dropLast(1).map { it.toLong() }, it.last()) }
    }

    fun parseInput2(input: List<String>): List<List<Char>> {
        val maxLength = input.maxOf { it.length }

        val transposed = (0 until maxLength).map { col ->
            input.indices.map { row ->
                if (row in input.indices && col in input[row].indices) {
                    input[row][col]
                } else {
                    ' '
                }
            }
        }

        val instructions = transposed.reversed().map { line -> line.filter { it != ' ' } }.filter { it.isNotEmpty() }

        return instructions
    }

    fun part2(input: List<String>): Long {
        val instructions = parseInput2(input)
        var sum = 0L
        var currentNumbers = mutableListOf<Long>()

        for (instruction in instructions) {
            currentNumbers.add(instruction.takeWhile { it.isDigit() }.joinToString("").toLong())

            if (!instruction.last().isDigit()) {
                sum += calculateAnswer(currentNumbers, instruction.last().toString())
                currentNumbers.clear()
            }
        }

        return sum
    }


    val testInput = readInput("day06/day06_test")
    check(part1(testInput), 4277556L)
    check(part2(testInput), 3263827L)

    val input = readInput("day06/day06")
    part1(input).println()
    part2(input).println()
}
