import java.io.File
import kotlin.math.pow

fun main() {
    val input = parse("input.txt")

    println("Part 01: ${part01(input)}")
    println("Part 02: ${part02(input)}")
}

fun part01(input: List<Int>): Int {

    val m = mutableMapOf<Int, Boolean>()
    input.forEach { m[it] = true }

    var adapterJolts = 0
    var diffs = IntArray(3)

    while(true) {
        val nextAdapterJolts = if (m.containsKey(adapterJolts + 1)) adapterJolts + 1
            else if (m.containsKey(adapterJolts + 2)) adapterJolts + 2
            else if (m.containsKey(adapterJolts + 3)) adapterJolts + 3
            else -1

        print(nextAdapterJolts - adapterJolts)

        if(nextAdapterJolts == -1) {
            diffs[3 - 1]++
            break
        }

        diffs[nextAdapterJolts - adapterJolts-1]++
        adapterJolts = nextAdapterJolts
    }

    diffs.forEach { println(it) }
    return diffs.last() * diffs.first()
}

fun part02(input: List<Int>): Long {

    // Group by diff = 1
    val sorted = input.sorted().toMutableList()
    sorted.add(0, 0)
    val groups = mutableListOf(mutableListOf(sorted[0]))
    for (i in 0 until sorted.size-1) {
        val diff = sorted[i+1] - sorted[i]
        if(diff == 1) {
            groups.last().add(sorted[i+1])
        } else {
            groups.add(mutableListOf(sorted[i+1]))
        }
    }

    // Group can be in 2^(size - 2) ways, except for size >= 5, which is 2^(size - 2) - 1
    return groups.
        map{ it.size.toLong() }.
        fold(1L){
            acc, it ->
                if (it <= 2) acc
                else acc *
                    (2.0.pow(it-2.0).toInt() +
                    if (it == 5L) - 1 else 0
                )
        }
}

fun parse(filename: String): List<Int> = File(filename).readLines().map { it.toInt() }

