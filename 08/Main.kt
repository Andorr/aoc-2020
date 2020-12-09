package `2`

import java.io.File

fun main() {
    val instructions = parse("input.txt")

    println(instructions)

    println("Part 01: ${part01(instructions)}")
    println("Part 02: ${part02(instructions)}")
}

fun part01(ins: List<Instruction>): Int {
    var acc: Int = 0
    var pc: Int = 0 // program counter

    var visited = mutableMapOf<Int, Int>()

    while(pc < ins.size) {
        val instruction = ins[pc]

        if (visited.containsKey(pc)) {
            return acc
        } else {
            visited[pc] = 1
        }

        when (instruction.op) {
            "nop" -> pc++
            "acc" -> {
                pc++
                acc += instruction.n
            }
            "jmp" -> pc += instruction.n
        }
    }

    return acc
}

fun part02(ins: List<Instruction>): Int {

    val tested = mutableSetOf<Int>()

    main@ while(true) {
        // Running program
        
        var acc: Int = 0
        var pc: Int = 0 // program counter

        val visited = mutableMapOf<Int, Int>()
        var switched = false
        
        // Make sure to copy the instructions, as we want to mutate for each run
        val insCpy = ins.map{ it.copy(it.op, it.n) }

        while(pc < ins.size) {
            val instruction = insCpy[pc]
            
            // Try to swap instruction op if it has not been changed before - only for "jmp" and "nop" instructions
            if (!switched && (instruction.op == "jmp" || instruction.op == "nop") && !tested.contains(pc)) {
                tested.add(pc)
                instruction.op = if (instruction.op == "jmp") "nop" else "jmp"
                switched = true
            }
            
            // Check if instruction has been visited before
            if (visited.containsKey(pc)) {
                continue@main
            } else {
                visited[pc] = 1
            }

            when (instruction.op) {
                "nop" -> pc++
                "acc" -> {
                    pc++
                    acc += instruction.n
                }
                "jmp" -> pc += instruction.n
            }
        }

        return acc
    }
}

fun parse(filename: String): List<Instruction> {
    return File(filename).readLines().map{
        val s = it.split(" ")
        Instruction(s[0], s[1].toInt())
    }
}

data class Instruction(var op: String, val n: Int)