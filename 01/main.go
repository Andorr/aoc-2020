package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"strconv"
)

func main() {
	input := readFile("input.txt")

	fmt.Printf("Part 01: %d\n", findPairWithSumN(input, 2020))
	fmt.Printf("Part 02: %d\n", findTripletWithSumN(input, 2020))
}

func findPairWithSumN(input []int, target int) int {

	m := map[int]int{}

	var e1, e2 int
	for _, v := range input {
		if val, ok := m[v]; ok {
			e1 = v
			e2 = val
			break
		}

		m[target-v] = v
	}

	return e1 * e2
}

func findTripletWithSumN(input []int, target int) int {

	m := map[int]int{}

	for _, v := range input {
		m[target-v] = v
	}

	var e1, e2, e3 int

exit:
	for _, v1 := range input {
		for _, v2 := range input {
			sum := v1 + v2
			if v3, ok := m[sum]; ok {
				e1 = v1
				e2 = v2
				e3 = v3
				break exit
			}
		}
	}

	return e1 * e2 * e3
}

func readFile(filename string) []int {
	file, err := os.Open(filename)
	if err != nil {
		log.Fatal(err.Error())
	}

	res := []int{}

	s := bufio.NewScanner(file)
	for s.Scan() {
		input := s.Text()
		n, _ := strconv.Atoi(input)
		res = append(res, n)
	}

	return res
}
