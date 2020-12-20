import java.io.File
import java.util.*
import kotlin.math.sqrt

const val IMG_SIZE = 10
val SEAMONSTER_PATTERN = arrayOf(
        Pair(0, 0),
        Pair(1, 1),
        Pair(1, 4),
        Pair(0, 5),
        Pair(0, 6),
        Pair(1, 7),
        Pair(1, 10),
        Pair(0, 11),
        Pair(0, 12),
        Pair(1, 13),
        Pair(1, 16),
        Pair(0, 17),
        Pair(0, 18),
        Pair(0, 19),
        Pair(-1, 18)
)
const val PIXEL_ON = '#'
const val PIXEL_SEAMONSTER = 'O'

enum class Border(i: Int) { Top(0), Right(1), Bottom(2), Left(3) }
data class TileOrientation(val id: Long, val pixels: List<CharArray>, val borders: Map<Border, String>)
data class Tile(val id: Long, val orientations: List<TileOrientation>)
data class PositionedTileOrientation(val x: Int, val y: Int, val t: TileOrientation)

fun main() {
    val input = parse("input.txt")

    val res = part01(input)
    println("Part 01: ${res.first}")
    println("Part 02: ${part02(res.second)}")
}

fun part01(input: List<Tile>): Pair<Long, Map<Pair<Int, Int>, PositionedTileOrientation>> {
    // Used BFS to generate the full image. Checked the neighbours with 4-connectivity, and tried to find
    // an unused tile that has at least one orientation that matches the borders of the existing neighbours.
    // With this said, all the possible orientations of a title is calculated on parse-time.

    val unusedTiles = input.toMutableList()
    val img = mutableMapOf<Pair<Int, Int>, PositionedTileOrientation>()

    // Create queue containing an arbitrary start tile
    val queue = LinkedList<PositionedTileOrientation>(listOf(PositionedTileOrientation(0, 0, unusedTiles[0].orientations[0])))
    unusedTiles.removeAt(0)

    // BFS
    while(queue.size > 0) {
        val curTm = queue.pop()

        // Mark tile as visisted
        if(!img.containsKey(Pair(curTm.x, curTm.y))) {
            img[Pair(curTm.x, curTm.y)] = curTm
        } else {
            // The tile has already been visited
            continue
        }

        // Push 4-connectivity neighbours to the queue if they match the neighbours of existing visited tiles
        listOf(
            Pair(1, 0),
            Pair(0, 1),
            Pair(-1, 0),
            Pair(0, -1)
        ).filter { pair ->
            val y = curTm.y + pair.first
            val x = curTm.x + pair.second

            // Filter visited tiles
            !img.containsKey(Pair(x, y))
        }
        .map {pair ->
            // Calculate image position of the potential tile
            val y = curTm.y + pair.first
            val x = curTm.x + pair.second
            val coord = Pair(x, y)

            // The adjacent borders at the given position
            val borderTop       = if (img.containsKey(Pair(x, y-1))) img[Pair(x, y-1)]!!.t.borders[Border.Bottom] else null
            val borderBottom    = if (img.containsKey(Pair(x, y+1))) img[Pair(x, y+1)]!!.t.borders[Border.Top] else null
            val borderLeft      = if (img.containsKey(Pair(x-1, y))) img[Pair(x-1, y)]!!.t.borders[Border.Right] else null
            val borderRight     = if (img.containsKey(Pair(x+1, y))) img[Pair(x+1, y)]!!.t.borders[Border.Left] else null

            // For each input - check for all possible transformations
            // if each transformation matches the adjacent tiles
            val matchingTileOrientation = unusedTiles.map { tile ->
                tile.orientations.firstOrNull() { orientation ->
                    val validBorders = mutableListOf<Boolean>()
                    if (borderTop != null) {
                        validBorders.add(orientation.borders[Border.Top] == borderTop)
                    }
                    if (borderBottom != null) {
                        validBorders.add(orientation.borders[Border.Bottom] == borderBottom)
                    }
                    if (borderRight != null) {
                        validBorders.add(orientation.borders[Border.Right] == borderRight)
                    }
                    if (borderLeft != null) {
                        validBorders.add(orientation.borders[Border.Left] == borderLeft)
                    }
                    validBorders.all { it }
                }
            }.firstOrNull { it != null }

            if(matchingTileOrientation != null) {
                // Remove found tile from pool
                unusedTiles.removeIf { tile -> tile.id == matchingTileOrientation.id }
            }

            // Return the position and the potential tile-match
            Pair(coord, matchingTileOrientation)
        }
        .filter { it.second != null } // Exclude the positions where no matches were found
        .forEach {
            val coord = it.first
            val tilePositionedTileOrientation = it.second!!

            // Add the found tile to the queue
            queue.add(PositionedTileOrientation(coord.first, coord.second, tilePositionedTileOrientation))
        }

    }

    // Calculate the corner coordinates
    val minX = img.keys.minBy { it.first }!!.first
    val maxX = img.keys.maxBy { it.first }!!.first
    val minY = img.keys.minBy { it.second }!!.second
    val maxY = img.keys.maxBy { it.second }!!.second

    // Return the product of all corner-coordinates
    return Pair(
            listOf(
                    img[Pair(minX, minY)]!!.t.id,
                    img[Pair(minX, maxY)]!!.t.id,
                    img[Pair(maxX, minY)]!!.t.id,
                    img[Pair(maxX, maxY)]!!.t.id
            )
            .fold(1L) { acc, it -> acc * it },
            img
    )
}

fun part02(img: Map<Pair<Int, Int>, PositionedTileOrientation>, verbose: Boolean = true): Long {
    // 1. Construct a new image with removed tile-borders
    // 2. For all orientations (rotations and flipped versions)
    //      Calculate number of sea monsters

    // Construct image into a single array of List<CharArray>
    val minX = img.keys.minBy { it.first }!!.first
    val maxX = img.keys.maxBy { it.first }!!.first
    val minY = img.keys.minBy { it.second }!!.second
    val maxY = img.keys.maxBy { it.second }!!.second

    // Order all tiles
    val tiles = mutableListOf<PositionedTileOrientation>()
    for(y in minY..maxY) {
        for(x in minX..maxX) {
            tiles.add(img[Pair(x, y)]!!)
        }
    }

    assert(tiles.size == 144)

    // Construct new image without tile-borders
    var im = mutableListOf<CharArray>()
    var imY = -1
    val size = sqrt(tiles.size.toFloat()).toInt()
    for (y in 0 until size * IMG_SIZE) {
        for(x in 0 until size) {
            val index = (y/IMG_SIZE) * size + (x)
            val transform = tiles[index]
            val yt = y%IMG_SIZE
            if(yt == 0 || yt == IMG_SIZE - 1) {
                // Top and bottom border
                continue
            }
            if(x == 0) {
                // The first part of a row
                im.add(CharArray(0))
                imY++
            }

            im[imY] = im[imY].plus(transform.t.pixels[yt].slice(1 until IMG_SIZE-1))
        }
    }

    assert(im.size == size)

    fun checkForSeaMonster(im: List<CharArray>, x: Int, y: Int): Boolean {
        for (pos in SEAMONSTER_PATTERN) {
            val xx = x + pos.second
            val yy = y + pos.first
            if(!(xx >= 0 && xx < im.size && yy >= 0 && yy < im.size)) {
                // No sea monster
                return false
            }

            val c = im[yy][xx]
            if(!(c == PIXEL_ON || c == PIXEL_SEAMONSTER)) {
                // No sea monster
                return false
            }
        }

        // Sea monster found!
        // Mark on map as 'O'
        for (pos in SEAMONSTER_PATTERN) {
            val xx = x + pos.second
            val yy = y + pos.first

            im[yy][xx] = PIXEL_SEAMONSTER
        }

        return true
    }

    fun calculateNumSeaMonsters(im: List<CharArray>): Long {
        var sumSeaMonsters = 0L
        for(y in im.indices) {
            for(x in im[y].indices) {
                if(checkForSeaMonster(im, x, y)) {
                    sumSeaMonsters++
                }
            }
        }
        return sumSeaMonsters
    }

    // Calculate number of sea monsters for each rotation and flip
    for(rot in 0..3) {
        var pixels = im.toMutableList().map { it.copyOf() }

        when(rot) {
            1 -> {
                // Rotate 90
                pixels = rotate90(pixels)
            }
            2 -> {
                // Rotate 180
                pixels = rotate90(rotate90(pixels))
            }
            3 -> {
                // Rotate 270 (-90)
                pixels = rotate90(rotate90(rotate90(pixels)))
            }
        }

        // No flip
        if(calculateNumSeaMonsters(pixels) > 0) {
            im = pixels.toMutableList()
            break
        }

        // Flip vertically
        val verticalFlip = flipPixels(pixels, false)
        if(calculateNumSeaMonsters(verticalFlip) > 0) {
            im = verticalFlip.toMutableList()
            break
        }

        // Flip horizontally
        val horizontalFlip = flipPixels(pixels, true)
        if(calculateNumSeaMonsters(horizontalFlip) > 0) {
            im = horizontalFlip.toMutableList()
            break
        }
    }

    if(verbose) {
        println()
        im.forEach { assert(it.size == size * IMG_SIZE) }
        for (i in 0 until im.size) {
            println(im[i])
        }
        println()
    }

    // Count number of '#'
    return im.fold(0L) { acc, it -> acc + it.fold(0L) { acc2, it2 -> acc2 + if(it2 == PIXEL_ON) 1 else 0 } }
}

fun generateAllOrientations(id: Long, tile: List<CharArray>): List<TileOrientation> {

    val orientations = mutableListOf<TileOrientation>()
    val size = tile.size

    for(rot in 0..3) {
        var pixels = tile.toMutableList().map { it.copyOf() }

        when(rot) {
            0 -> {
                // No rotation
            }
            1 -> {
                // Rotate 90
                pixels = rotate90(pixels)
            }
            2 -> {
                // Rotate 180
                pixels = rotate90(rotate90(pixels))
            }
            3 -> {
                // Rotate 270 (-90)
                pixels = rotate90(rotate90(rotate90(pixels)))
            }
        }

        // Flip vertically
        val verticalFlip = flipPixels(pixels, false)

        // Flip horizontally
        val horizontalFlip = flipPixels(pixels, true)

        orientations.add(TileOrientation(id, pixels, calculateBorders(pixels)))
        orientations.add(TileOrientation(id, verticalFlip, calculateBorders(verticalFlip)))
        orientations.add(TileOrientation(id, horizontalFlip, calculateBorders(horizontalFlip)))
    }

    return orientations
}

fun calculateBorders(pixels: List<CharArray>): Map<Border, String> {
    return mapOf<Border, String>(
            Border.Top to pixels[0].fold("") { acc, c -> acc + c },
            Border.Bottom to pixels.last().fold("") { acc, c -> acc + c },
            Border.Right to pixels.map { it.last() }.fold("") { acc, c -> acc + c },
            Border.Left to pixels.map { it.first() }.fold("") { acc, c -> acc + c }
    )
}

fun rotate90(pixels: List<CharArray>): List<CharArray> {
    val to = pixels.toMutableList().map { it.copyOf() }

    val size = pixels.size

    for(i in 0 until size) {
        for(j in 0 until size) {
            to[j][size - 1 - i] = pixels[i][j]
        }
    }
    return to
}

fun flipPixels(pixels: List<CharArray>, horizontal: Boolean): List<CharArray> {
    val size = pixels.size

    if(horizontal) {
        val horizontalFlip = pixels.toMutableList().map { it.copyOf() }
        for(i in 0 until size) {
            for(j in 0 until size/2) {
                val temp = horizontalFlip[i][j]
                horizontalFlip[i][j] = horizontalFlip[i][size-1-j]
                horizontalFlip[i][size-1-j] = temp
            }
        }
        return horizontalFlip
    }

    val verticalFlip = pixels.toMutableList().map { it.copyOf() }
    for(i in 0 until size) {
        for(j in 0 until size/2) {
            val temp = verticalFlip[j][i]
            verticalFlip[j][i] = verticalFlip[size-1-j][i]
            verticalFlip[size-1-j][i] = temp
        }
    }
    return verticalFlip
}

fun parse(filename: String): List<Tile> {
    return File(filename)
            .readText()
            .split("\n\n")
            .filter { it != "" }
            .map {
                val pixels = it.split("\n").drop(1).map { it.toCharArray() }
                val id = it.substring("Tile ".length, "Tile ".length + 4).toLong()
                Tile(
                    id,
                    generateAllOrientations(id, pixels)
                )
            }
}