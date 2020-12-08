package main

import (
	"bufio"
	"fmt"
	"os"
)

func main() {
	input := parseInput("input.txt")
	fmt.Println(input)

	// fmt.Printf("Part 01: %d\n", questionSums(input))
	fmt.Printf("Part 02: %d\n", questionSums(input))
}

func questionSums(input [][]string) int {
	var sum int

	for _, group := range input {
		m := map[rune]int{}

		for _, line := range group {
			for _, c := range line {
				m[c]++
			}
		}

		var groupSum int
		for _, value := range m {
			if value == len(group) {
				groupSum++
			}
		}

		sum += groupSum
	}

	return sum
}

func parseInput(fileName string) [][]string {

	input := [][]string{}
	i := 0

	file, _ := os.Open(fileName)
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()

		if line == "" {
			i++
			continue
		}

		if len(input) <= i {
			input = append(input, []string{})
		}

		input[i] = append(input[i], line)
	}
	/*
		for _, s := range strings.Split(string(bytes), "\n") {
			input = append(input, strings.Split(s, "\n"))
			fmt.Println(s)
		} */

	return input
}
