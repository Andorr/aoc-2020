import java.io.File
import java.util.*

fun main() {
    val input = parse("input.txt")

    println("Part01: ${part01(input)}")
    println("Part02: ${part02(input)}")
}

fun part01(input: List<Group>): Long {
    return input.map { it.solve() }.sum()
}

fun part02(input: List<Group>): Long {
    return input.map {
        it.groupAddition()
        it.solve()
    }.sum()
}


//typealias Operator = Char
enum class Operator(c: Char) {
    ADD('+'),
    MUL('*'),
}
data class Group(var terms: MutableList<Any>) {
    constructor(): this(mutableListOf())

    fun groupAddition() {
        // Goes through every group and merges the terms next to the addition operation into a single group
        var i = 0
        while(i < terms.size) {
            val entity = terms[i]
            if(entity == Operator.ADD) {
                // Merge into a single group
                val b = terms.removeAt(i+1)
                val a = terms.removeAt(i-1)
                if(a is Group) {
                    a.groupAddition()
                }
                if(b is Group) {
                    b.groupAddition()
                }
                i-- // removeAt(i-1) decrements the current index by 1
                terms[i] = Group(mutableListOf(a, Operator.ADD, b))
            }
            else if(entity is Group) {
                entity.groupAddition()
            }
            i++
        }
    }

    fun solve(): Long {
        var answer: Long = -1
        var op: Operator? = null

        for (i in 0 until terms.size) {
            val entity: Any = terms[i]

            if(entity is Operator) {
                op = entity
                continue
            }

            var entityNumber = if(entity is Group) entity.solve() else entity as Long
            if(op == null) {
                answer = entityNumber
            }
            else {
                when(op) {
                    Operator.MUL -> answer *= entityNumber
                    Operator.ADD -> answer += entityNumber
                }
            }
        }

        return answer
    }
}
fun parse(filename: String): List<Group>  {
    val numReg = Regex("\\d+")
    val opReg = Regex("[+*]")

    return File(filename).readLines().map { line ->
        val stack = Stack<Group>()
        var curGroup = Group()
        line.split(" ").forEach {
            if(numReg.matches(it)) {
                // Is number
                curGroup.terms.add(it.toLong())
            }
            else if(opReg.matches(it)) {
                // Is operation
                curGroup.terms.add(if (it[0] == '+') Operator.ADD else Operator.MUL)
            }
            else {
                // Is "((?\d+" or "\d+))?"
                for (c in it) {
                    when (c) {
                        '(' -> {
                            // Start new group
                            stack.push(curGroup)
                            curGroup = Group()
                        }
                        ')' -> {
                            // End group
                            val oldGroup = stack.pop()
                            oldGroup.terms.add(curGroup)
                            curGroup = oldGroup
                        }
                        else -> {
                            // Is number
                            curGroup.terms.add(c.toString().toLong())
                        }
                    }
                }

            }
        }
        curGroup
    }
}