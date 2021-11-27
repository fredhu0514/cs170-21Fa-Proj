package test

import (
	"fmt"
	"math/rand"
)

func Solve(tasks []Task) ([]int64, error) {
	// Set random seed
	rand.Seed(int64(2023))

	// Init parameters here
	var MaxIteration int = 3

	// Init a State Map
	StateMap := map[string]State{}
	// Init Cur Transition record
	CurTransition := []TransitionUnit{}

	profit, maxPath, err := Train(MaxIteration, &StateMap, &CurTransition, &tasks)
	if err != nil {
		return nil, err
	}
	fmt.Println("Max path:")
	fmt.Println(maxPath)
	fmt.Println("Profit:")
	fmt.Println(profit)
	return maxPath, nil
}