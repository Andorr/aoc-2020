import java.io.File

fun main() {
    val input = parse("input.txt")

    println("Part 01: ${part01(input)}")
    println("Part 02: ${part02(input)}")
}

fun part01(input: SatelliteInfo): Long {
    val rules = input.rules.toMutableMap()

    fun matchRule(ruleId: Int, message: String, index: Int): Pair<Boolean, Int> {
        val rule = rules[ruleId]!!
        var i = index

        if(message.length <= i) {
            return Pair(false, i)
        }

        if(rule.c != null) {
            return Pair(rule.c == message[i], i+1)
        }

        if(rule.subRules != null) {
            return Pair(rule.subRules.any {
                var j = index
                var didAllMatch = it.all { newRuleId ->
                    val matchRes = matchRule(newRuleId, message, j)
                        j = matchRes.second
                    matchRes.first
                }
                if (didAllMatch) {
                    i = j
                }
                didAllMatch
            }, i)
        }

        return Pair(false, i)
    }

    return input.messages
        .map { message ->
            val match = matchRule(0, message, 0)
            Triple(match.first && message.length == match.second, match.second, message)
        }
        .filter{ it.first }
        .count().toLong()
}

fun part02(input: SatelliteInfo): Long {
    // This solution is quite awful, as it is hardcoded only for rule 0: 8 11, where
    // rule 8 and 11 loops themselves.

    // It is possible to create new rules for 8 and 11 where they do not loop, by repeating the loop pattern N times
    // where N is the limit. For example:
    //  8: 42 | 42 42 | 42 42 42 | 42 42 42 42 | 42 42 42 42 42...
    // 11: 42 31 | 42 42 31 31 | 42 42 42 31 31 31 | 42 42 42 42 31 31 31 31 | 42 42 42 42 42 31 31 31 31 31...

    val rules = input.rules.toMutableMap()

    // Update the rules
    // rules[8] = Rule(listOf(listOf(42), listOf(42, 8)), null)
    // rules[11] = Rule(listOf(listOf(42, 31), listOf(42, 11, 31)), null)

    fun matchRule(ruleId: Int, message: String, index: Int): Pair<Boolean, Int> {
        val rule = rules[ruleId]!!
        var i = index

        if(message.length <= i) {
            return Pair(false, i)
        }

        if (ruleId == 0) {
            return matchRule(8, message, i)
        }

        if(ruleId == 8) {
            // Message matches if it matches 42 X times, and then match rule 11
            // Brute force X times
            var match = matchRule(42, message, i)
            while(match.first) {
                i = match.second

                // Is it possible to match rule 11 now?
                match = matchRule(11, message, i)
                if(match.first && message.length == match.second) {
                    return Pair(true, match.second)
                }

                // If not, try matching rule 42 again
                match = matchRule(42, message, i)
            }
            return match
        }

        if(ruleId == 11) {
            // Rule 11 matches regex = (R42)*N + (R31)*N
            // Check if it matches rule 42 N times, then match rule 31 N times
            var match: Pair<Boolean, Int>
            var N = 0
            while(true) {
                match = matchRule(42, message, i)
                if(match.first) {
                    N++
                    i = match.second
                } else {
                    break
                }
            }
            if(N == 0) {
                // Rule 11 requires at least 1 match of rule 42 and rule 31
                return Pair(false, i)
            }
            while(N > 0) {
                match = matchRule(31, message, i)
                if(match.first) {
                    N--
                    i = match.second
                } else {
                    return Pair(false, i)
                }
            }

            return Pair(true, i)
        }

        if(rule.c != null) {
            return Pair(rule.c == message[i], i+1)
        }

        if(rule.subRules != null) {
            return Pair(rule.subRules.any {
                var j = index
                var didAllMatch = it.all {newRuleId ->
                    val matchRes = matchRule(newRuleId, message, j)
                    j = matchRes.second
                    matchRes.first
                }
                if (didAllMatch) {
                    i = j
                }
                didAllMatch
            }, i)
        }

        return Pair(false, i)
    }

    return input.messages
            .map { message ->
                val match = matchRule(0, message, 0)
                Triple(match.first && message.length == match.second, match.second, message)
            }
            .filter{ it.first }
            .count().toLong()
}

data class Rule(val subRules: List<List<Int>>?, val c: Char?)
typealias Rules = Map<Int,Rule>
data class SatelliteInfo(val rules: Rules, val messages: List<String>)
fun parse(filename: String): SatelliteInfo {

    val rules = mutableMapOf<Int, Rule>()
    val messages = mutableListOf<String>()
    var parseRules = true

    File(filename).readLines().forEach { line ->
        if(line == "") {
            parseRules = false
            return@forEach
        }

        if(parseRules) {
            line
                .split(": ")
                .let {
                    val subRules = it[1]
                            .takeIf { !it.contains("\"") }
                            ?.split(" | ")
                            ?.map { subrule ->
                                subrule
                                    .split(" ")
                                    .map { it.toInt() }
                            }
                    val s = it[1].takeIf{ it.startsWith("\"") }?.let { it[1] }
                    rules[it[0].toInt()] = Rule(subRules, s)
                }
        } else {
            messages.add(line)
        }
    }

    return SatelliteInfo(rules, messages)
}