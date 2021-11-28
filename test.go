package main

import (
	Test "./test"
	"log"
	"math/rand"
	"os"
	"strconv"
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
	rand.Seed(int64(20003))
	path := []string{"small", "medium", "large"}
	for s:=0; s<=2; s++ {
		for index:=1; index<=300; index++ {
			log.Printf("READY TO GO: ")
			taskFileName := "./inputs/" + path[s] + "/" + path[s] + "-" +strconv.Itoa(index) + ".in"
			rawOutputFileName := "./outputs/" + path[s] + "/" + path[s] + "-" +strconv.Itoa(index) + ".out"
			newOutputFileName := "./newoutput/" + path[s] + "/" + path[s] + "-" +strconv.Itoa(index) + ".out"
			log.Println("task file: " + taskFileName)
			log.Println("raw output: " + rawOutputFileName)
			log.Println("new output: " + newOutputFileName)

			// Read in Tasks
			taskListPTR, err := Test.ReadInputFile(taskFileName)
			if err != nil {
				panic(err)
			}

			// Read in Output
			rawOPListPTR, err := Test.ReadOutputFile(rawOutputFileName)
			if err != nil {
				panic(err)
			}

			// Task into Solve
			outputList, err := Test.Solve(taskListPTR, rawOPListPTR, 20000)
			if err != nil {
				panic(err)
			}

			// Write the tasks order
			err = Test.WriteOutputFile(newOutputFileName, outputList)
			if err != nil {
				panic(err)
			}
		}
	}
}