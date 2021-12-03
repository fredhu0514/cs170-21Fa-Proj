package TimeState

func Iteration(tasks *[]Task) *[]int {
	metric := InitMetric(tasks)
	realTasks := *tasks
	for curRealTime:=1; curRealTime<1440; curRealTime++ {
		curTimePTR := curRealTime % Time_Length
		for curID:=0; curID<len(realTasks); curID++ {
			// If current task duration + current real time > 1440; abort
			if int(realTasks[curID].duration) + curRealTime > 1440 {
				metric.Matrix[curTimePTR][curID].Profit = -1.0
				continue
			}

			// Default values
			maxPrevProfit := -10.0
			maxPrevPathPTR := &([]int{})

			// See past ids
			for prevID:=0; prevID<len(realTasks); prevID++ {
				// prevID cannot equal to current id; abort
				if prevID == curID {
					continue
				}
				// Previous Time cannot smaller than 0; abort
				timeDiff := curRealTime - int(realTasks[prevID].duration)
				if timeDiff < 0 {
					continue
				}
				// profit at (Previous Time, prevID) < 0; abort
				prevTimePTR := timeDiff % Time_Length
				prevProfit := metric.Matrix[prevTimePTR][prevID].Profit
				if prevProfit < 0.0 {
					continue
				}
				// if curID is in the path at (Previous Time, prevID); abort
				if SliceInArray(curID, &(metric.Matrix[prevTimePTR][prevID].Path)) {
					continue
				}

				// Satisfies all conditions
				if maxPrevProfit < prevProfit {
					maxPrevProfit = prevProfit
					maxPrevPathPTR = &(metric.Matrix[prevTimePTR][prevID].Path)
				}
			}

			// If none previous can reach current state; abort
			if maxPrevProfit < 0.0 {
				metric.Matrix[curTimePTR][curID].Profit = -1.0
				continue
			}

			// Exist a max prev state
			metric.Matrix[curTimePTR][curID].Profit = maxPrevProfit + realTasks[curID].GetProfit(int64(curRealTime))
			metric.Matrix[curTimePTR][curID].Path = metric.Matrix[curTimePTR][curID].Path[:0] // Empty it first
			metric.Matrix[curTimePTR][curID].Path = append(metric.Matrix[curTimePTR][curID].Path, *maxPrevPathPTR...) // Prev Path
			metric.Matrix[curTimePTR][curID].Path = append(metric.Matrix[curTimePTR][curID].Path, curID) // CurID

			// ** Global Compare
			if metric.MaxProfit < metric.Matrix[curTimePTR][curID].Profit {
				metric.MaxProfit = metric.Matrix[curTimePTR][curID].Profit
				metric.MaxPath = metric.MaxPath[:0]
				metric.MaxPath = append(metric.MaxPath, metric.Matrix[curTimePTR][curID].Path...)
			}
		}
	}
	return &(metric.MaxPath)
}

func SliceInArray(slice int, array *[]int) bool {
	for i := range *array {
		if slice == (*array)[i] {
			return true
		}
	}
	return false
}