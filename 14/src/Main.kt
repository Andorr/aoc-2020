import java.io.File
import kotlin.math.pow

data class Allocation(val address: Long, val value: Long)
data class Allocations(val mask: String, val allocations: MutableList<Allocation>)

fun main() {
    var input = parse("input.txt")

    println("Part 01: ${part01(input)}")
    println("Part 02: ${part02(input)}")
}

fun part01(input: List<Allocations>): Long {

    val mem = mutableMapOf<Long, Long>()

    input.forEach {
        val mask = it.mask
                .mapIndexed { index, c -> Pair(index, c) }
                .filter { it.second != 'X' }
                .map { p -> Pair(it.mask.length - 1 - p.first, p.second.toString().toLong()) }
        it.allocations.forEach { a ->
            mem[a.address] = mask.fold(a.value) { acc, it ->
                if (it.second == 0L) acc and (1L shl it.first).inv() else acc or (1L shl it.first )
            }
        }
    }

    return mem.values.sum()
}

fun part02(input: List<Allocations>): Long {

    val mem = mutableMapOf<Long, Long>()

    input.forEach {
        // Masks of implicit bits
        val mask = it.mask
                .mapIndexed { index, c -> Pair(index, c) }
                .filter { it.second != 'X' }
                .map { p -> Pair(it.mask.length - 1 - p.first, p.second.toString().toLong()) }

        // Masks of Xs
        val floatingMasks = it.mask
                .mapIndexed { index, c -> Pair(index, c) }
                .filter { it.second == 'X' }
                .map { p -> it.mask.length - 1 - p.first }

        it.allocations.forEach { a ->
            // Set bits by implicit masks
            val maskedAddress = mask.fold(a.address) { acc, it ->
                if (it.second == 0L) acc else acc or (1L shl it.first)
            }

            // Calculate all possible address with x
            val numCombinations = 2.0.pow(floatingMasks.size)
            var i = 0
            while(i < numCombinations) {
                val newAddress = floatingMasks.foldIndexed(maskedAddress) { index, address, shiftIndex  ->
                    if (i and (1 shl index) == 0) address and (1L shl shiftIndex).inv() else address or (1L shl shiftIndex )
                }

                mem[newAddress] = a.value
                i++ // Calculate next combination
            }
        }
    }

    return mem.values.sum()
}

fun parse(filename: String): List<Allocations> {
    val lines = File(filename).readLines()

    val regex = Regex("mem\\[(\\d+)\\] = (\\d+)")

    val allocations = mutableListOf<Allocations>()
    var i = -1
    lines.forEach {
        if(it.startsWith("mask = ")) {
            allocations.add(Allocations(it.removePrefix("mask = "), mutableListOf<Allocation>()))
            i++
            return@forEach
        }
        val v = regex.matchEntire(it)!!.groupValues
        allocations[i].allocations.add(Allocation(v[1].toLong(), v[2].toLong()))
    }
    return allocations
}