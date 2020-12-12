import java.io.File
import kotlin.math.absoluteValue

enum class Direction(str: String) {
    N("N"),
    S("S"),
    E("E"),
    W("W"),
    F("F"),
    R("R"),
    L("L"),
}

data class Move(val dir: Direction, val steps: Int)

fun main() {
    val input = parse("input.txt")
    println("Part 01: ${part01(input)}")
    println("Part 02: ${part02(input)}")
}

fun part01(input: List<Move>): Int {

    data class Ferry(var x: Int, var y: Int, var rot: Int)

    val finalCoords = input.fold(Ferry(0, 0, 0)) { acc, move ->
        when (move.dir) {
            Direction.E -> acc.x += move.steps
            Direction.W -> acc.x -= move.steps
            Direction.N -> acc.y += move.steps
            Direction.S -> acc.y -= move.steps
            Direction.R -> acc.rot = (acc.rot + move.steps)%360
            Direction.L -> {
                acc.rot = (acc.rot - move.steps)%360
                if(acc.rot < 0) {
                    // Keep rotations positive
                    acc.rot += 360
                }
            }
            Direction.F -> {
                when (acc.rot) {
                    0 -> acc.x += move.steps
                    90 -> acc.y -= move.steps
                    180 -> acc.x -= move.steps
                    270 -> acc.y += move.steps
                }
            }
        }
        acc
    }

    return finalCoords.x.absoluteValue + finalCoords.y.absoluteValue
}

fun part02(input: List<Move>): Int {


    data class Transform(var x: Int, var y: Int, var rot: Int)
    data class Ferry(val t: Transform, val waypoint: Transform)

    val finalCoords = input.fold(Ferry(Transform(0, 0, 0), Transform(10, 1, 0))) { acc, move ->
        when (move.dir) {
            Direction.E -> acc.waypoint.x += move.steps
            Direction.W -> acc.waypoint.x -= move.steps
            Direction.N -> acc.waypoint.y += move.steps
            Direction.S -> acc.waypoint.y -= move.steps
            Direction.R, Direction.L -> {

                var dir = if(move.dir == Direction.R) 1 else -1
                if(move.steps == 180) {
                    dir = 1 // There is no difference between R and L for 180
                }

                val curX = acc.waypoint.x * dir
                val curY = acc.waypoint.y * dir
                when (move.steps) {
                    90 -> {
                        acc.waypoint.x = curY
                        acc.waypoint.y = -curX
                    }
                    180 -> {
                        acc.waypoint.x = -curX
                        acc.waypoint.y = -curY
                    }
                    270 -> {
                        acc.waypoint.x = -curY
                        acc.waypoint.y = curX
                    }
                }
            }
            Direction.F -> {
                acc.t.x += move.steps * acc.waypoint.x
                acc.t.y += move.steps * acc.waypoint.y
            }
        }
        acc
    }

    return finalCoords.t.x.absoluteValue + finalCoords.t.y.absoluteValue
}

fun parse(filename: String) = File(filename).readLines().map { Move(Direction.valueOf(it.substring(0, 1)), it.substring(1).toInt()) }

