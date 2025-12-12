package day04

import Point
import check
import plus
import println
import readInput

fun main() {
    class Paper(val position: Point, val neighborCount: Int)

    operator fun List<String>.get(position: Point) =
        if (position.first in this.indices && position.second in this[0].indices) this[position.first][position.second] else null

    fun String.replaceCharAt(index: Int, newChar: Char): String {
        return this.substring(0, index) + newChar + this.substring(index + 1)
    }

    fun countNeighbors(input: List<String>, position: Point): Int {
        val neighborLocations = (-1..1).flatMap { row -> (-1..1).map { col -> row to col } }
        return neighborLocations.map { it + position }.count { input[it] == '@' && it != position }
    }

    fun parseInput(input: List<String>): List<Paper> {
        return input.withIndex().flatMap { (rowIdx, row) ->
            row.withIndex().mapNotNull { (colIdx, char) ->
                if (char == '@')
                    Paper(rowIdx to colIdx, countNeighbors(input, rowIdx to colIdx))
                else null
            }
        }
    }

    fun part1(input: List<String>): Int {
        val paperRolls = parseInput(input)
        return paperRolls.count { it.neighborCount < 4 }
    }

    fun part2(input: List<String>): Int {
        var map = input.toMutableList()
        var removedRolls = 0

        var rollsToRemove: List<Paper>
        do {
            val paperRolls = parseInput(map)
            rollsToRemove = paperRolls.filter { it.neighborCount < 4 }

            for (roll in rollsToRemove) {
                map[roll.position.first] = map[roll.position.first].replaceCharAt(roll.position.second, '.')
                removedRolls++
            }
        } while (rollsToRemove.isNotEmpty())


        return removedRolls
    }


    val testInput = readInput("day04/day04_test")
    check(part1(testInput), 13)
    check(part2(testInput), 43)

    val input = readInput("day04/day04")
    part1(input).println()
    part2(input).println()
}
