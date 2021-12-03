package main

import (
	TS "./TimeState"
	"log"
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

func main() {
	path := []string{"small", "medium", "large"}
	for s:=0; s<=3; s++ {
		for index:=1; index<=300; index++ {
			log.Printf("READY TO GO: ")
			taskFileName := "./inputs/" + path[s] + "/" + path[s] + "-" +strconv.Itoa(index) + ".in"
			outputFileName := "./outputs/" + path[s] + "/" + path[s] + "-" +strconv.Itoa(index) + ".out"
			log.Println("task file: " + taskFileName)
			log.Println("output dir: " + outputFileName)

			// Read in Tasks
			taskListPTR, err := TS.ReadInputFile(taskFileName)
			if err != nil {
				panic(err)
			}

			// Task into Solve
			outputListPTR := TS.Solver(taskListPTR)

			// Write the tasks order
			err = TS.WriteOutputFile(outputFileName, *outputListPTR)
			if err != nil {
				panic(err)
			}
		}
	}
}