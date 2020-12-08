package `1`

import java.io.File

fun main() {
    println("Part 01: ${part01("input.txt")}")
    println("Part 02: ${part02("input.txt")}")
}

fun part01(filename: String): Int {
    val m = parse(filename)

    var targets = m.entries.filter { it.value.any{ l -> l.key == "shiny gold" } }.map { it.key }
    val visitedBags = mutableMapOf<String, Boolean>()

    while(targets.isNotEmpty()) {
        targets.forEach { visitedBags.put(it, true) }

        targets = m.entries
                .filter{ it.value.any{ l -> targets.contains(l.key) }}
                .filter{ !visitedBags.containsKey(it.key) }
                .map{ it.key }
    }

    return visitedBags.size
}

fun part02(filename: String): Int {
    val m = parse(filename)

    fun numBags(m: Map<String, List<Match>>, target: String): Int {
        val bags = m[target]!!
        if (bags.size == 0) {
            return 1
        }
        return bags.map{ numBags(m, it.key) * it.n }.sum() + 1
    }

    return numBags(m, "shiny gold") - 1 // Exclude the actual shiny bag
}

fun parse(filename: String): Map<String, List<Match>> {
    val m = mutableMapOf<String, List<Match>>()

    val r = Regex("((\\d)+ ([a-zA-Z ]+)bags?[\\., ]+)")
    File(filename).forEachLine() {
        val line = it.split(" bags contain ")
        val targetBag = line[0]
        m.put(targetBag, (r.findAll(line[1]).map {
            Match(it.groups[3]!!.value.trim(), it.groups[2]!!.value.toInt())
        }.toList()))
    }
    return m
}

data class Match(val key: String, val n: Int)
