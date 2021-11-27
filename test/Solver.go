package test

import (
	"fmt"
)
var lr float64 = 0.0001
var reward float64 = -0.001
var gamma float64 = 0.85

func Solve(PTRTaskList *([]Task)) ([]int64, error){
	profit, path, c := Train(100000, PTRTaskList)
	if c != nil {
		panic(c)
	}
	fmt.Println("Profit")
	fmt.Println(profit)
	fmt.Println("Path")
	fmt.Println(path)
	return path, nil
}
