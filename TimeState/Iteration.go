package TimeState

import (
	"errors"
	"fmt"
)

var SPACE_CONSTANT int = 120

func Iteration(tasks *[]Task) *[]int {
	matrixPTR := NewMetric(tasks)
	matrixPTR = matrixPTR.Preprocessing(tasks)
	for curTime:=1; curTime<1440; curTime++ {
		for curIDPTR:=0; curIDPTR<len(*tasks); curIDPTR++ {
			// Set time Index to mod SPACE_CONSTANT
			curTimePTR := curTime % SPACE_CONSTANT

			// If cur task duration plus cur time > 1440 set the profit to -1
			curTaskDuartion := (*tasks)[curIDPTR].duration
			if curTaskDuartion + int64(curTime) > int64(1440) {
				(*matrixPTR.Pivots)[curIDPTR][curTimePTR].Profit = -1.0 // Set the time infeasible profit to -1
				continue
			}

			// Another iteration through all tasks
			maxID := -1
			maxProfit := -1.0
			var maxPathPTR *[]int
			for prevIDPTR:=0; prevIDPTR<len(*tasks); prevIDPTR++ {
				// If current task is the same as the prev task
				if prevIDPTR == curIDPTR {
					continue
				}

				// Prev Task start time
				prevRealTime := curTime - int((*tasks)[prevIDPTR].duration)
				if prevRealTime < 0 {
					continue
				}
				prevTime := ((prevRealTime % SPACE_CONSTANT) + SPACE_CONSTANT) % SPACE_CONSTANT

				// Check if that position is valid (reachable)
				prevProfit := (*matrixPTR.Pivots)[prevIDPTR][prevTime].Profit
				if prevProfit < 0 { // Invalid (unreachable)
					continue
				}

				// Check if curID is in the path of Previous
				meetSameCurID := false
				prevPathPTR := (*matrixPTR.Pivots)[prevIDPTR][prevTime].Path
				for psIndex:=0; psIndex<len(*prevPathPTR); psIndex++ {
					if (*prevPathPTR)[psIndex] == curIDPTR {
						meetSameCurID = true
						break
					}
				}
				if meetSameCurID {
					continue
				}

				// current metric is at least valid
				if prevProfit > maxProfit {
					maxProfit = prevProfit
					maxID = prevIDPTR
					maxPathPTR = prevPathPTR
				}
			}

			// If through max the maxProfit and maxID are still default, means this point is not reachable
			if maxProfit < 0 {
				if maxID >= 0 {
					panic(errors.New("inconsistent of maxProfit and maxID"))
				}
				// set current to unreachable
				(*matrixPTR.Pivots)[curIDPTR][curTimePTR].Profit = -1.0 // Set the time infeasible profit to -1
				continue
			}

			// Else, there is a valid value, retrieve and re-assign
			if maxID < 0 {
				panic(errors.New("inconsistent of maxProfit and maxID, and this is even more weird"))
			}
			(*matrixPTR.Pivots)[curIDPTR][curTimePTR].Profit = maxProfit + (*tasks)[curIDPTR].GetProfit(int64(curTime))
			// TODO: DEBUG
			fmt.Println(*maxPathPTR, curIDPTR)
			for iii := range *maxPathPTR {
				if curIDPTR == (*maxPathPTR)[iii] {
					fmt.Println(curIDPTR, *maxPathPTR)
					panic(errors.New("SHOULD NOT"))
				}
			}
			for jjj := 0; jjj < len(*maxPathPTR) - 1; jjj++ {
				for kkk := jjj + 1; kkk<len(*maxPathPTR); kkk++ {
					if (*maxPathPTR)[jjj] == (*maxPathPTR)[kkk] {
						panic(errors.New("DAMN1"))
					}
 				}
			}
			newPath := append(*maxPathPTR, curIDPTR)
			for jjj := 0; jjj < len(newPath) - 1; jjj++ {
				for kkk := jjj + 1; kkk<len(newPath); kkk++ {
					if (newPath)[jjj] == (newPath)[kkk] {
						panic(errors.New("DAMN2"))
					}
				}
			}
			(*matrixPTR.Pivots)[curIDPTR][curTimePTR].Path = &newPath

			// ** Check if this is the largest value ever
			if (*matrixPTR.Pivots)[curIDPTR][curTimePTR].Profit > *matrixPTR.LargestProfit {
				// fmt.Println("NEW MAX HERE", (*matrixPTR.Pivots)[curIDPTR][curTimePTR].Profit)
				*matrixPTR.LargestProfit = (*matrixPTR.Pivots)[curIDPTR][curTimePTR].Profit
				matrixPTR.LargestPath = (*matrixPTR.Pivots)[curIDPTR][curTimePTR].Path
			}

		}
	}
	fmt.Println(*matrixPTR.LargestProfit)
	return matrixPTR.LargestPath
}