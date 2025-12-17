package day09

import check
import println
import readInput
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun main() {

    fun parseInput(input: List<String>): List<Pair<Long, Long>> {
        return input.map { it.split(",").map { it.toLong() } }.map { it.first() to it.last() }
    }

    fun part1(input: List<String>): Long {
        val redTiles = parseInput(input)

        val rectangleAreas = redTiles.indices.flatMap { i ->
            (i + 1 until redTiles.size).map { j ->
                val dr = redTiles[i].first - redTiles[j].first
                val dc = redTiles[i].second - redTiles[j].second

                (abs(dr) + 1) * (abs(dc) + 1)
            }
        }

        return rectangleAreas.max()
    }

    data class Polygon(
        val xIndex: Map<Long, Int>,
        val yIndex: Map<Long, Int>,
        private val prefixSums: List<List<Long>>
    ) {
        // Number of inside cells within the rectangle defined by [rowStart, rowEnd) x [colStart, colEnd)
        fun insideCells(rowStart: Int, rowEnd: Int, colStart: Int, colEnd: Int): Long {
            val areaToBottomRight = prefixSums[rowEnd][colEnd]
            val areaAbove = prefixSums[rowStart][colEnd]
            val areaLeft = prefixSums[rowEnd][colStart]
            // has to be added back because it was subtracted twice
            val areaTopLeft = prefixSums[rowStart][colStart]

            return areaToBottomRight - areaAbove - areaLeft + areaTopLeft
        }

        fun containsRectangle(c1: Pair<Long, Long>, c2: Pair<Long, Long>): Boolean {
            val bottom = min(c1.first, c2.first)
            val top = max(c1.first, c2.first)
            val left = min(c1.second, c2.second)
            val right = max(c1.second, c2.second)

            // The tested rectangle must align with the polygon grid which is the case here because the rectangle is
            // constructed from polygon vertices.
            // The other two missing corners will always have either the same row or column as one of the polygon
            // vertices so they will also be in the indices.
            val rowStart = yIndex[bottom] ?: return false
            val rowEnd = yIndex[top] ?: return false
            val colStart = xIndex[left] ?: return false
            val colEnd = xIndex[right] ?: return false

            val cellsInThePolygon = insideCells(rowStart, rowEnd, colStart, colEnd)
            val cellsInTheRectangle = (rowEnd - rowStart) * (colEnd - colStart)
            return cellsInThePolygon == cellsInTheRectangle.toLong()
        }
    }

    /**
     * Build prefix sum in 2D. Value at (r, c) is the number of 'inside' cells in the rectangle from (0,0) to (r-1,c-1).
     *
     * Take the rectangle above the current cell and the rectangle left of the current cell and subtract the overlapping top-left rectangle.
     * Then add 1 if the current cell is inside the polygon.
     */
    fun calculatePrefixSum(grid: List<List<Boolean>>): List<List<Long>> {
        val rowCount = grid.size
        val colCount = grid[0].size

        val prefix = List(rowCount + 1) { MutableList(colCount + 1) { 0L } }
        for (row in 1..rowCount) {
            for (col in 1..colCount) {
                val prefixAbove = prefix[row - 1][col]
                val prefixLeft = prefix[row][col - 1]
                val prefixTopLeft = prefix[row - 1][col - 1]
                // Indexing inside by -1 because the left/top border of the prefix sum is padding
                val currentCellValue = if (grid[row - 1][col - 1]) 1 else 0
                prefix[row][col] = prefixAbove + prefixLeft - prefixTopLeft + currentCellValue
            }
        }

        return prefix
    }

    /**
     * Ray casting algorithm to determine if a point is inside a polygon
     *
     * Polygon given as list of (row, col) points in convex order
     */
    fun isPointInPolygon(col: Double, row: Double, polygon: List<Pair<Double, Double>>): Boolean {
        var inside = false

        // Ray is cast to the right
        for (i in 0 until polygon.size) {
            val (rowI, colI) = polygon[i]
            val (rowJ, colJ) = polygon[(i + 1) % polygon.size]

            // If both rows of the vertices are on the same side of the ray, no crossing because the ray is horizontal
            val edgeCrossesRow = (rowI > row) != (rowJ > row)

            val horizontalGradient = (colJ - colI) / (rowJ - rowI)
            val distanceToRay = row - rowI
            val colValueAtRay = horizontalGradient * distanceToRay + colI

            // If colValueAtRay is grater than col, then the ray will cross the edge, other it has already passed it
            if (edgeCrossesRow && col < colValueAtRay) {
                inside = !inside
            }
        }
        return inside
    }

    fun coordinateCompression(polygon: List<Pair<Long, Long>>): Polygon {
        val yCoords = polygon.map { it.first }.distinct().sorted()
        val xCoords = polygon.map { it.second }.distinct().sorted()
        val rowCount = yCoords.size - 1
        val colCount = xCoords.size - 1

        val polygonPoints = polygon.map { it.first.toDouble() to it.second.toDouble() }
        val inside = List(rowCount) { MutableList(colCount) { false } }
        for (row in 0 until rowCount) {
            val yMid = (yCoords[row] + yCoords[row + 1]) / 2.0
            for (col in 0 until colCount) {
                val xMid = (xCoords[col] + xCoords[col + 1]) / 2.0
                // Because all edges are axis-aligned, meaning they are either horizontal or vertical,
                // checking the midpoint is sufficient to determine if the entire cell is inside the polygon,
                // because all edges run along the grid lines.
                inside[row][col] = isPointInPolygon(xMid, yMid, polygonPoints)
            }
        }

        val prefix = calculatePrefixSum(inside)
        val yIndex = yCoords.withIndex().associate { it.value to it.index }
        val xIndex = xCoords.withIndex().associate { it.value to it.index }

        return Polygon(xIndex, yIndex, prefix)
    }

    fun part2(input: List<String>): Long {
        val redTiles = parseInput(input)
        val polygon = coordinateCompression(redTiles)
        var maxArea = 0L

        for (i in 0 until redTiles.lastIndex) {
            for (j in i + 1 until redTiles.size) {
                val c1 = redTiles[i]
                val c2 = redTiles[j]

                val rectangleArea = (abs(c1.first - c2.first) + 1) * (abs(c1.second - c2.second) + 1)
                if (rectangleArea <= maxArea) continue
                if (polygon.containsRectangle(c1, c2)) maxArea = rectangleArea
            }
        }

        return maxArea
    }

    val testInput = readInput("day09/day09_test")
    check(part1(testInput), 50)
    check(part2(testInput), 24)

    val input = readInput("day09/day09")
    part1(input).println()
    part2(input).println()
}
