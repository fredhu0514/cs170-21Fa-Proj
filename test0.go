package main

import (
	Test "./test"
	"log"
	"os"
)

func init() {
	log.SetFlags(log.Ldate | log.Lmicroseconds | log.Llongfile)
	logFile, err := os.OpenFile("./test.log", os.O_CREATE | os.O_RDWR | os.O_APPEND,0644)
	if err  != nil {
		log.Panic("CANNOT OPEN LOG FILE")
	}
	log.SetOutput(logFile)
}

func main(){
	log.Printf("READY TO GO: ")
	path := "./input/small/small-1.in"
	log.Println(path)
	taskListPTR, err := Test.ReadInputFile(path)
	if err != nil {
		panic(err)
	}
	outputList, err := Test.Solve(taskListPTR, 200000)
	if err != nil {
		panic(err)
	}
	err = Test.WriteOutputFile("./shit.out", outputList)
	if err != nil {
		panic(err)
	}
}