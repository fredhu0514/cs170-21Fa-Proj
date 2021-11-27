package main

import (
	Test "./test"
	// "fmt"
	"math/rand"
)

func main(){
	// Set random seed
	rand.Seed(int64(2023))

	path := "./input/small/small-1.in"
	taskListPTR, err := Test.ReadInputFile(path)
	if err != nil {
		panic(err)
	}
	outputList, err := Test.Solve(taskListPTR)
	if err != nil {
		panic(err)
	}
	err = Test.WriteOutputFile("./shit.out", outputList)
	if err != nil {
		panic(err)
	}
}