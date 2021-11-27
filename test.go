package main

import (
	Test "./test"
)

func main(){
	//inputLengthStringList := []string{"small", "medium", "large"}
	//inputPath := "./input/"
	//for j := range inputLengthStringList {
	//	for i:=1; i<=300; i++ {
	//		fileName := inputPath + inputLengthStringList[j] + "/" + inputLengthStringList[j] + "-" + strconv.Itoa(i) + ".in"
	//		ttlist, err := Test.ReadInputFile(fileName)
	//		if err != nil {
	//			panic(err)
	//		}
	//		fmt.Println(ttlist)
	//	}
	//}

	path := "./input/small/small-1.in"
	taskList, err := Test.ReadInputFile(path)
	if err != nil {
		panic(err)
	}
	outputList, err := Test.Solve(taskList)
	if err != nil {
		panic(err)
	}
	err = Test.WriteOutputFile("./shit.out", outputList)
	if err != nil {
		panic(err)
	}
}