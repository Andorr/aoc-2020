import kotlin.system.measureTimeMillis

var input = listOf(7,12,1,0,16,2)
var test = listOf(0,3,6)

fun main() {
    println("Part 01: ${nthNumber(input, 2020)}")

    var result = 0
    val timeUsed = measureTimeMillis {
        result = nthNumber(input, 30000000)
    }
    println("Part 02: $result - Time: ${timeUsed/1000.0}s")
}

fun nthNumber(input: List<Int>, N: Int = 2020): Int {
    val visited = mutableMapOf<Int, Pair<Int, Int>>()

    input.forEachIndexed { index, i ->  visited[i] = Pair(index + 1, -1)}

    var prevNum = input.last()
    var turn = input.size + 1
    while(turn <= N) {
        var nextNum = 0
        if(visited[prevNum]?.let { it.first > 0 && it.second > 0 } == true) {
            nextNum = visited[prevNum]!!.let { it.first - it.second }
        }
        visited[nextNum] = Pair(turn, visited[nextNum]?.first ?: 0)

        prevNum = nextNum
        turn++
    }

    return prevNum
}
