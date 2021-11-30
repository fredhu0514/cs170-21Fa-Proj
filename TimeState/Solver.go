package TimeState

import (
	"fmt"
	"math/rand"
)

var lr float64 = 0.01
var reward float64 = 0
var gamma float64 = 0.85
var maxIteration int = 200000

func Solver(tasks *[]Task) (*[]int64) {
	rand.Seed(20010514)
	profit, maxPathPTR, err := Iteration(tasks, maxIteration)
	if err != nil {
		panic(err)
	}
	fmt.Println(profit)
	return maxPathPTR
}
