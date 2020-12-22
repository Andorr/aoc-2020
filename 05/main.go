package main

import (
	"fmt"
	"io/ioutil"
	"strings"
)

func main() {
	input := parseInput("input.txt")

	fmt.Printf("Part 01: %d\n", boardingPassHighestID(input))
	fmt.Printf("Part 02: %d\n", findMissingSeatID(input))
}

func boardingPassHighestID(boardingPasses []string) int {

	highestID := 0
	bestRow := 0
	bestCol := 0

	for _, bp := range boardingPasses {

		rowStart := 0
		rowEnd := 127

		colStart := 0
		colEnd := 7

		for i, c := range bp {
			if len(bp)-i > 3 {
				if c == 'F' {
					rowEnd -= (rowEnd-rowStart)/2 + 1
				} else if c == 'B' {
					rowStart += (rowEnd-rowStart)/2 + 1
				}
			} else {
				if c == 'L' {
					colEnd -= (colEnd-colStart)/2 + 1
				} else if c == 'R' {
					colStart += (colEnd-colStart)/2 + 1
				}
			}
		}

		id := rowStart*8 + colStart
		if id > highestID {
			highestID = id
			bestRow = rowStart
			bestCol = colStart
		}
	}

	fmt.Printf("(%d, %d)\n", bestRow, bestCol)

	return highestID
}

func findMissingSeatID(boardingPasses []string) int {

	seats := [128][8]int{}

	firstRow := 128

	for _, bp := range boardingPasses {

		rowStart := 0
		rowEnd := 127

		colStart := 0
		colEnd := 7

		for i, c := range bp {
			if len(bp)-i > 3 {
				if c == 'F' {
					rowEnd -= (rowEnd-rowStart)/2 + 1
				} else if c == 'B' {
					rowStart += (rowEnd-rowStart)/2 + 1
				}
			} else {
				if c == 'L' {
					colEnd -= (colEnd-colStart)/2 + 1
				} else if c == 'R' {
					colStart += (colEnd-colStart)/2 + 1
				}
			}
		}

		seats[rowStart][colStart] = 1

		if rowStart < firstRow {
			firstRow = rowStart
		}
	}

	// Find missing seat
	for i := firstRow + 1; i < 128; i++ {
		for j := 0; j < 7; j++ {
			if seats[i][j] == 0 {
				return i*8 + j
			}
		}
	}

	return -1
}

func parseInput(filename string) []string {
	bytes, _ := ioutil.ReadFile(filename)
	return strings.Split(string(bytes), "\n")
}
