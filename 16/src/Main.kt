import java.io.File

fun main() {
    println("Hello World :D")

    val input = parse("input.txt")
    println("Part 01: ${part01(input)}")
    println("Part 02: ${part02(input)}")
}

fun part01(input: TicketInfo): Int {

    val invalidValues = mutableListOf<Int>()

    for (ticket in input.nearbyTickets) {

        // Check if ticket == 0
        ticket@ for (n in ticket) {
            for(rule in input.rules) {
                for(range in rule.ranges) {
                    if(range.first <= n && n <= range.second) {
                        // Is valid
                        continue@ticket
                    }
                }
            }
            // Is invalid
            invalidValues.add(n)
        }
    }

    return invalidValues.sum()
}

fun part02(input: TicketInfo): Long {
    // Filter away invalid tickets
    val nearbyTickets = input.nearbyTickets.filter { ticket ->
        ticket@ for (n in ticket) {
            for(rule in input.rules) {
                for(range in rule.ranges) {
                    if(range.first <= n && n <= range.second) {
                        // Is valid
                        continue@ticket
                    }
                }
            }

            // Is invalid
            return@filter false
        }
        return@filter true
    }

    val rules = input.rules.toMutableList()
    val m = mutableMapOf<Int, Rule>()
    while(rules.size > 0) {
        field@ for (fieldIndex in nearbyTickets[0].indices) {
            if(m.containsKey(fieldIndex)) {
                continue@field
            }

            // Find unknown field
            var matchingRules = rules.filter { rule ->
                nearbyTickets.all { ticket -> rule.ranges.any { it.first <= ticket[fieldIndex] && ticket[fieldIndex] <= it.second } }
            }

            if(matchingRules.size == 1) {
                m[fieldIndex] = matchingRules[0]
                rules.removeAt(rules.indexOfFirst{ rule -> rule.name == matchingRules[0].name})
            }
        }
    }

    return m.entries
            .filter { e -> e.value.name.startsWith("departure") }
            .map { input.ticket[it.key] }
            .fold(1) {acc, it -> acc * it}.toLong()
}

data class Rule(val name: String, val ranges: List<Pair<Int, Int>>)
data class TicketInfo(val rules: List<Rule>, val ticket: List<Int>, val nearbyTickets: List<List<Int>>)
fun parse(filename: String): TicketInfo {

    val lines = File(filename).readLines()

    val rules = mutableListOf<Rule>()
    val tickets = mutableListOf<List<Int>>()
    var myTicket = mutableListOf<Int>()

    var mode = 0
    val ruleRegex = Regex("(\\d+)-(\\d+)")
    for (line in lines) {

        if(line == "") {
            mode++
            continue
        }
        if(line.startsWith("your ticket") || line.startsWith("nearby tickets")) {
            continue
        }


        when (mode) {
            0 -> {
                rules.add(line.split(": ").let{ parts -> Rule(parts[0],
                    parts[1].split(" or ").map{
                        ruleRegex.matchEntire(it)!!.groupValues
                    }.map { Pair(it[1].toInt(), it[2].toInt()) }
                )})
            }
            1 -> {

                myTicket = line.split(",").map { it.toInt() }.toMutableList()
            }
            2 -> {
                tickets.add(line.split(",").map { it.toInt() }.toMutableList())
            }
        }



    }



    return TicketInfo(rules, myTicket, tickets)
}