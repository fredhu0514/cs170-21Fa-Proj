package test

import (
	"log"
)
var lr float64 = 0.01
var reward float64 = -0.0001
var gamma float64 = 0.85

func Solve(PTRTaskList *([]Task), MaxIter int) ([]int64, error){
	log.Printf("Max Iteration: %d", MaxIter)
	log.Printf("lr %f; reward %f; gamma %f",  lr, reward, gamma)
	profit, path, c := Train(MaxIter, PTRTaskList)
	if c != nil {
		panic(c)
	}
	log.Printf("Profit: %f", profit)
	log.Println(path)
	return path, nil
}
