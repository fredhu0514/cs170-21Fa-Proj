package main

import (
	Test "./test"
	"fmt"
	"strconv"
)

func main(){
	inputLengthStringList := []string{"100/", "150/", "200/"}
	inputPath := "./input/"
	for j := range inputLengthStringList {
		for i:=1; i<51; i++ {
			fileName := inputPath + inputLengthStringList[j] + "sample" + strconv.Itoa(i) + ".in"
			ttlist, err := Test.ReadInputFile(fileName)
			if err != nil {
				panic(err)
			}
			fmt.Println(ttlist)
		}
	}
}