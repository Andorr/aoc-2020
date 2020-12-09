package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strings"
)

func main() {

	// Part 1
	m, width, height := parseInput("input.txt")
	fmt.Printf("Part 1: %d\n", numTreesInSlope(m, width, height, 0, 0, 3, 1))

	// Part 2
	numTrees := numTreesInSlope(m, width, height, 0, 0, 1, 1)
	numTrees *= numTreesInSlope(m, width, height, 0, 0, 3, 1)
	numTrees *= numTreesInSlope(m, width, height, 0, 0, 5, 1)
	numTrees *= numTreesInSlope(m, width, height, 0, 0, 7, 1)
	numTrees *= numTreesInSlope(m, width, height, 0, 0, 1, 2)

	fmt.Printf("Part 2: %d\n", numTrees)
}

func numTreesInSlope(m string, width, height int, startX int, startY int, dx int, dy int) int {

	curX := startX
	curY := startY

	numTrees := 0

	for curY < height {
		if m[curX+curY*width] == '#' {
			numTrees++
		}

		curX = (curX + dx) % width
		curY += dy
	}

	return numTrees
}

func parseInput(filename string) (string, int, int) {

	file, err := os.Open(filename)
	if err != nil {
		log.Fatal(err.Error())
	}

	output := strings.Builder{}
	scan := bufio.NewScanner(file)
	height := 0
	width := 0
	for scan.Scan() {
		line := scan.Text()
		output.WriteString(line)
		if height == 0 {
			width = len(line)
		}
		height++
	}

	return output.String(), width, height
}
