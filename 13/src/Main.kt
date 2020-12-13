import java.io.File
import kotlin.system.measureTimeMillis

fun main() {
    val input = parse("input.txt")
    println("Part 01: ${part01(input)}")

    var timestamp: Long = 0
    val timeUsed = measureTimeMillis {
        timestamp = part02(input)
    }
    println("Part 02: ${timestamp} - Time: ${timeUsed}ms")
}

fun part01(busInfo: BusInfo): Long {

    var closestTimestamp = Long.MAX_VALUE
    var closestBusId = 0L
    for (bus in busInfo.busIds) {
        var i = 0;
        while(true) {
            i++
            if(i * bus >= busInfo.timestamp) {
               val diff = i * bus - busInfo.timestamp
                if (diff < closestTimestamp) {
                    closestTimestamp = diff
                    closestBusId = bus
                }
                break
            }
        }
    }
    
    return closestBusId * closestTimestamp
}

fun part02(info: BusInfo): Long {

    /*
    var jump = 19*821
    var jump2 = 41*29
    var jump3 = 37*50
    */

    var sliceEnd = 1
    var inc = info.busIds[0] // Common divisor from bus 0 until sliceEnd
    var timestamp = inc //start - (start%i)
    main@ while(true) {
        timestamp += inc

        // Find the next common timestamp
        for (i in 0 until sliceEnd + 1) {
            val busId = info.busIds[i]
            val minutes = info.indices[i]

            if ((timestamp + minutes)%busId != 0L) {
                continue@main
            }
        }

        sliceEnd++

        if(sliceEnd == info.busIds.size) {
            return timestamp
        }
        inc = info.busIds.slice(0 until sliceEnd).fold(1L) { acc, it -> acc * it }
    }
}

data class BusInfo(val timestamp: Long, val busIds: List<Long>, val indices: List<Long>)
fun parse(filename: String): BusInfo {
    val lines = File(filename).readLines()
    return BusInfo(
            lines[0].toLong(),
            lines[1].split(",").filter { it != "x" }.map { it.toLong() },
            lines[1].split(",").mapIndexed { index, s ->  Pair(index, s) }.filter { it.second != "x" }.map{ it.first.toLong() }
    )
}