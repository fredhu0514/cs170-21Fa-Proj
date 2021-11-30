package main

import (
	TS "./TimeState"
	"strconv"
)


func main() {
	taskFileName := "./inputs/" + "small" + "/" + "small" + "-" +strconv.Itoa(73) + ".in"
	newOutputFileName := "./testOP/" + "small" + "/" + "small" + "-" +strconv.Itoa(73) + ".out"

	// Read in Tasks
	taskListPTR, err := TS.ReadInputFile(taskFileName)
	if err != nil {
		panic(err)
	}

	output := TS.Solver(taskListPTR)

	// Write the tasks order
	err = TS.WriteOutputFile(newOutputFileName, *output)
	if err != nil {
		panic(err)
	}
}