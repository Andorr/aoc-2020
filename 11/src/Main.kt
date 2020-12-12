import java.io.File

enum class Seat(val s: Char) {
    Occupied('#'),
    Available('L'),
    Floor('.'),
}

fun main() {
    val input = parse("input.txt")

    println("Part 01: ${part01(input)}")
    println("Part 01: ${part02(input)}")
}

fun part01(input: Array<CharArray>): Int {

    val width = input[0].size
    val height = input.size

    //  Create new copy of input
    //var prev = input.toMutableList().map { it.toMutableList() }
    var seats = input.toMutableList().map { it.toMutableList() } // mutableListOf<MutableList<Char>>()
    var isNotEqual = true
    while(isNotEqual) {
        var prev = seats.toMutableList().map { it.toMutableList() }

        for (i in 0 until height) {
            for(j in 0 until width) {
                val numAdjecentOccupied = arrayOf(-1, 0, 1).fold(0) { acc, i2 ->
                    acc + arrayOf(-1, 0 ,1).fold(0) { acc2, j2 ->
                        val y = i + i2
                        val x = j + j2
                        if((i2 == 0 && j2 == 0) || !(x >= 0 && x < width && y >= 0 && y < height)) {
                           return@fold acc2
                        }
                        acc2 + if (prev[y][x] == Seat.Occupied.s) 1 else 0
                    }
                }

                val curSeat = prev[i][j]

                if (curSeat == Seat.Available.s && numAdjecentOccupied == 0) {
                    seats[i][j] = Seat.Occupied.s
                } else if(curSeat == Seat.Occupied.s && numAdjecentOccupied >= 4) {
                    seats[i][j] = Seat.Available.s
                }
            }
        }

        isNotEqual = false
        main@ for(i in 0 until height) {
            for(j in 0 until width) {
                if (seats[i][j] != prev[i][j]) {
                    isNotEqual = true
                    break@main
                }
            }
        }
    }


    return seats.fold(0) { acc, it -> acc + it.fold(0) { acc2, it2 -> acc2 + if (it2 == Seat.Occupied.s) 1 else 0 } }
}

fun part02(input: Array<CharArray>): Int {

    val width = input[0].size
    val height = input.size

    //  Create new copy of input
    var seats = input.toMutableList().map { it.toMutableList() } // mutableListOf<MutableList<Char>>()
    var isNotEqual = true
    while(isNotEqual) {
        var prev = seats.toMutableList().map { it.toMutableList() }

        for (i in 0 until height) {
            for(j in 0 until width) {
                val numAdjecentOccupied = {
                    fun countOccupiedInDirection(dx: Int, dy: Int): Int {
                        var count = 0
                        var s = 1

                        while(i + s*dy >= 0 && j + s*dx >= 0 && i + s*dy < height && j + s*dx < width) {
                            val seat = prev[i+s*dy][j+s*dx]
                            if(seat == Seat.Occupied.s) {
                                count++
                                break
                            } else if(seat == Seat.Available.s) {
                                break
                            }

                            s++
                        }
                        return count
                    }

                    var c = countOccupiedInDirection(1, 0)
                    c += countOccupiedInDirection( -1, 0)
                    c += countOccupiedInDirection( 0, 1)
                    c += countOccupiedInDirection( 0, -1)
                    c += countOccupiedInDirection( 1, 1)
                    c += countOccupiedInDirection( 1, -1)
                    c += countOccupiedInDirection( -1, 1)
                    c += countOccupiedInDirection( -1, -1)
                    c
                }()

                val curSeat = prev[i][j]

                if (curSeat == Seat.Available.s && numAdjecentOccupied == 0) {
                    seats[i][j] = Seat.Occupied.s
                } else if(curSeat == Seat.Occupied.s && numAdjecentOccupied >= 5) {
                    seats[i][j] = Seat.Available.s
                }
            }
        }

        isNotEqual = false
        main@ for(i in 0 until height) {
            for(j in 0 until width) {
                if (seats[i][j] != prev[i][j]) {
                    isNotEqual = true
                    break@main
                }
            }
        }
    }


    return seats.fold(0) { acc, it -> acc + it.fold(0) { acc2, it2 -> acc2 + if (it2 == Seat.Occupied.s) 1 else 0 } }
}

fun parse(filename: String): Array<CharArray> = File(filename).readLines().map { it.toCharArray() }.toTypedArray()
