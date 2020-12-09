package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"regexp"
	"strconv"
)

type Password struct {
	start    int
	end      int
	c        rune
	password string
}

func main() {
	passwords := parseInput("input.txt")
	fmt.Printf("Part 01: %d\n", calcNumValidPasswords(passwords))
	fmt.Printf("Part 02: %d\n", calcActualNumValidPasswords(passwords))
}

func calcNumValidPasswords(passwords []*Password) int {
	var numValid int

	for _, pass := range passwords {

		runeFreqs := map[rune]int{}
		for _, c := range pass.password {
			runeFreqs[c]++
		}

		if val := runeFreqs[pass.c]; val >= pass.start && val <= pass.end {
			numValid++
		}
	}

	return numValid
}

func calcActualNumValidPasswords(passwords []*Password) int {
	var numValid int

	for _, pass := range passwords {

		first := (rune(pass.password[pass.start-1]) == pass.c)
		last := (rune(pass.password[pass.end-1]) == pass.c)
		if (first || last) && !(first && last) {
			numValid++
		}
	}

	return numValid
}

func parseInput(fileName string) []*Password {

	file, err := os.Open(fileName)
	if err != nil {
		log.Fatal(err.Error())
	}

	r, err := regexp.Compile("(\\d+)-(\\d+) (\\w): (.*)")
	if err != nil {
		log.Fatal(err.Error())
	}

	passwords := []*Password{}
	scan := bufio.NewScanner(file)
	for scan.Scan() {
		line := scan.Text()
		params := r.FindStringSubmatch(line)
		start, _ := strconv.Atoi(params[1])
		end, _ := strconv.Atoi(params[2])
		passwords = append(passwords, &Password{
			start:    start,
			end:      end,
			c:        rune(params[3][0]),
			password: params[4],
		})
	}

	return passwords
}
