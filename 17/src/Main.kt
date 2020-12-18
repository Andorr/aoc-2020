import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val input = parse("input.txt")
    
    println("Part 01: ${part01(input, 6)}")

    var result: Long = 0
    val timeUsed = measureTimeMillis {
        result = part02(input, 6)
    }
    println("Part 02: $result - Time: ${timeUsed/1000.0}s")
}

fun part01(input: Grid, cycles: Long = 3L, verbose: Boolean = false): Long {

    var prev: MutableMap<String, Boolean> = input.grid.toMutableMap()
    var minX = 0L
    var maxX = input.size
    var minY = 0L
    var maxY = input.size
    var minZ = 0L
    var maxZ = 1L


    for (cycle in 0 until cycles) {
        val m = mutableMapOf<String, Boolean>() //prev.toMutableMap()

        // Expand the search grid
        if(cycle > 0) {
            minX--
            maxX++
            minY--
            maxY++
        }
        minZ--
        maxZ++

        for(z in minZ..maxZ) {
            for(y in minY..maxY) {
                for(x in minX..maxX) {
                    val coord = coord(y, x, z)
                    val isActive = prev[coord] == true

                    // Check all neighbours
                    var numActiveNeighbours = 0
                    for(ky in -1L..1L) {
                        for(kx in -1L..1L) {
                            for(kz in -1L..1L) {
                                if(kz == 0L && ky == 0L && kx == 0L) {
                                    continue
                                }

                                val neighbourCoord = coord(y + ky, x + kx, z + kz)
                                if(prev[neighbourCoord] == true) {
                                    numActiveNeighbours++ // Active neighbour found
                                }
                            }
                        }
                    }

                    if(isActive && (numActiveNeighbours == 2 || numActiveNeighbours == 3)) {
                        // Remain active
                        m[coord] = true
                    } else if(numActiveNeighbours == 3) {
                        // Become active
                        m[coord] = true
                    }
                }
            }
        }

        prev = m

        if(verbose) {
            println("Cycle: ${cycle + 1}")
            print(prev, minX - 2, maxX + 2, minY - 2, maxY + 2, minZ - 2, maxZ + 2)
        }
    }

    return prev.values.filter { it }.count().toLong()
}

fun part02(input: Grid, cycles: Long = 3L): Long {

    var prev: MutableMap<String, Boolean> = input.grid.toMutableMap()
    var minX = 0L
    var maxX = input.size
    var minY = 0L
    var maxY = input.size
    var minZ = 0L
    var maxZ = 1L
    var minW = 0L
    var maxW = 1L


    for (cycle in 0 until cycles) {
        val m = mutableMapOf<String, Boolean>()

        // Expand the search grid
        if(cycle > 0) {
            minX--
            maxX++
            minY--
            maxY++
        }
        minZ--
        maxZ++
        minW--
        maxW++

        for(w in minW..maxW) {
            for(z in minZ..maxZ) {
                for(y in minY..maxY) {
                    for(x in minX..maxX) {
                        val coord = coord(y, x, z, w)
                        val isActive = prev[coord] == true

                        // Check all neighbours
                        var numActiveNeighbours = 0
                        for(ky in -1L..1L) {
                            for(kx in -1L..1L) {
                                for(kz in -1L..1L) {
                                    for(kw in -1L..1L) {
                                        if(kz == 0L && ky == 0L && kx == 0L && kw == 0L) {
                                            continue
                                        }

                                        val neighbourCoord = coord(y + ky, x + kx, z + kz, w + kw)
                                        if(prev[neighbourCoord] == true) {
                                            numActiveNeighbours++
                                        }
                                    }
                                }
                            }
                        }

                        if(isActive && (numActiveNeighbours == 2 || numActiveNeighbours == 3)) {
                            // Remain active
                            m[coord] = true
                        } else if(numActiveNeighbours == 3) {
                            // Become active
                            m[coord] = true
                        }
                    }
                }
            }
        }

        prev = m
    }

    return prev.values.filter { it }.count().toLong()
}

fun print(m: MutableMap<String, Boolean>, minX: Long, maxX: Long, minY: Long, maxY: Long, minZ: Long, maxZ: Long) {
    for(z in minZ until maxZ) {
        println("Z = $z")
        for (y in minY until maxY) {
            for (x in minX until maxX) {
                val coord = coord(y, x, z)
                val str = if(m.containsKey(coord) && m[coord]!!) '#' else '.'
                print(str)
            }
            println()
        }
    }
}

fun coord(y: Long, x: Long, z: Long, w: Long = 0): String {
    return "$y-$x-$z-$w"
}

data class Grid(val grid: MutableMap<String, Boolean>, val size: Long)
fun parse(filename: String): Grid {

    val m = mutableMapOf<String, Boolean>()
    var size = 0L
    File(filename)
            .readLines()
            .also { size = it[0].length.toLong() }
            .map { it.toCharArray() }
            .forEachIndexed { y, charArray ->
                charArray.forEachIndexed { x, c ->
                    m[coord(y.toLong(), x.toLong(), 0)] = c == '#'
                }
            }
    return Grid(m, size)
}