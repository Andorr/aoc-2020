import java.io.File

fun main() {
    val input = parse("input.txt")
    println("Part 01: ${part01(input, 25)}")
    println("Part 02: ${part02(input, "1721308972".toLong())}")
    println("Part 02: ${part02_n2(input, "1721308972".toLong())}")
}

fun part01(input: List<Long>, preambleSize: Int): Long {

    var i = preambleSize

    main@ while(i < input.size) {
        val target = input[i]

        // Extract the last N numbers
        val lastNNum = input.slice(i-preambleSize..i)

        val m = mutableMapOf<Long, Long>()
        for (it in lastNNum) {
            if (m.containsKey(it)) {
                i++
                continue@main // Found pair
            } else {
                m[target - it] = it
            }
        }

        return target
    }

    return -1
}

fun part02(input: List<Long>, target: Long): Long {
    var i = 0
    var j = 0

    while(true) {
        val sum = input.slice(i..j+1).reduce{ acc, it -> acc + it}

        if(sum < target) {
            j++
        } else if (sum > target) {
            i++
        } else {
            break
        }
    }

    // Find lowest and highest within range i,j
    return input.slice(i..j+1)
            .map { Pair(it, it) }
            .reduce { acc, pair -> Pair(
                    if (pair.first < acc.first) pair.first else acc.first,
                    if (pair.second > acc.second) pair.second else acc.second
            ) }
            .also {
                println("(i, j) = (${it.first}, ${it.second})")
            }
            .let { it.first + it.second }
}

fun part02_n2(input: List<Long>, target: Long): Long {

    for (i in 0..input.size) {
        var j = i
        var sum = input[i]

        var smallest = input[i]
        var biggest = input[i]

        while(sum < target) {
            j++
            sum += input[j]

            if(input[j] < smallest) {
                smallest = input[j]
            }
            if(input[j] > biggest) {
                biggest = input[j]
            }
        }

        if (sum == target) {
            println("S-B: (${smallest}, ${biggest})")
            return smallest + biggest
        }
    }

    return -1
}



fun parse(filename: String): List<Long> {
    return File(filename).readLines().map{ it.toLong() }
}