package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"regexp"
	"strconv"
	"strings"
)

func main() {
	passports := parsePassportsFromFile("input.txt")
	fmt.Println(passports)
	fmt.Println(len(passports))

	fmt.Printf("Part 01: %d\n", countValidPassports(passports))
	fmt.Printf("Part 01: %d\n", countValidPassportsSeriously(passports))
}

func countValidPassports(passports []map[string]string) int {

	countValidPassports := 0

	requiredKeys := []string{"byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"}

passportLoop:
	for _, passport := range passports {

		for _, key := range requiredKeys {
			if _, ok := passport[key]; !ok {
				continue passportLoop
			}

		}
		countValidPassports++
	}

	return countValidPassports
}

func countValidPassportsSeriously(passports []map[string]string) int {

	countValidPassports := 0

	requiredKeys := []string{"byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"}

passportLoop:
	for _, passport := range passports {

		for _, key := range requiredKeys {
			value, ok := passport[key]
			if !ok {
				continue passportLoop
			}

			switch key {
			case "byr":
				{
					year, _ := strconv.Atoi(value)
					if len(value) != 4 || !(year >= 1920 && year <= 2002) {
						continue passportLoop
					}
				}
			case "iyr":
				{
					year, _ := strconv.Atoi(value)
					if len(value) != 4 || !(year >= 2010 && year <= 2020) {
						continue passportLoop
					}
				}
			case "eyr":
				{
					year, _ := strconv.Atoi(value)
					if len(value) != 4 || !(year >= 2020 && year <= 2030) {
						continue passportLoop
					}
				}
			case "hgt":
				{
					metric := value[len(value)-2:]
					height, _ := strconv.Atoi(value[:len(value)-2])

					if (metric != "cm" && metric != "in") || (metric == "cm" && !(height >= 150 && height <= 193)) || (metric == "in" && !(height >= 59 && height <= 76)) {
						continue passportLoop
					}
				}
			case "hcl":
				{
					if matched, _ := regexp.Match("#[0-9a-f]{6}", []byte(value)); !matched {
						continue passportLoop
					}
				}
			case "ecl":
				{
					acceptedValues := map[string]bool{"amb": true, "blu": true, "brn": true, "gry": true, "grn": true, "hzl": true, "oth": true}
					if _, ok := acceptedValues[value]; !ok {
						continue passportLoop
					}
				}
			case "pid":
				{
					_, err := strconv.Atoi(value)
					if len(value) != 9 || err != nil {
						continue passportLoop
					}
				}
			}

		}
		countValidPassports++
	}

	return countValidPassports
}

func parsePassportsFromFile(fileName string) []map[string]string {

	file, err := os.Open(fileName)
	if err != nil {
		log.Fatal(err.Error())
	}

	m := make([]map[string]string, 0)
	i := 0

	scanner := bufio.NewScanner(file)
	for scanner.Scan() {

		line := scanner.Text()

		if len(line) == 0 {
			i++
			continue
		}

		if len(m) <= i {
			m = append(m, map[string]string{})
		}

		keyValuePairs := strings.Split(line, " ")
		for _, kv := range keyValuePairs {

			kvSplit := strings.Split(kv, ":")
			m[i][kvSplit[0]] = kvSplit[1]
		}

	}

	return m
}
