package TimeState

import (
	"errors"
	"fmt"
	"math/rand"
	"strconv"
)

func Iteration(tasks *[]Task, maxIteration int) (float64, *[]int64, error) {
	STATE_MAP := NewStateMap(tasks)

	maxProfit := float64(0)
	maxProfitPath := []int64{}

	REAL_COUNTER := 0
	for curIter:=0;curIter<maxIteration;curIter++ {
		profit, timeListPTR, pathPTR := STATE_MAP.Forward(tasks, curIter, maxIteration)
		if curIter % 10000 == 0 {
			fmt.Println("n0000", REAL_COUNTER, curIter, profit, *pathPTR, "\n")
		}

		if profit >= maxProfit {
			err := STATE_MAP.Backward(maxProfit, timeListPTR, pathPTR, tasks)
			if err != nil {
				return 0, nil, err
			}

			maxProfit = profit
			maxProfitPath = *pathPTR
			fmt.Println("NEW MAX", REAL_COUNTER, curIter, maxProfit, maxProfitPath)

			// STATE_MAP = NewStateMap(tasks)
			for i:=0; i<10000; i++ {
				_, timeListPTR, pathPTR = STATE_MAP.GuideForward(tasks, &maxProfitPath)
				err := STATE_MAP.Backward(maxProfit, timeListPTR, pathPTR, tasks)
				if err != nil {
					return 0, nil, err
				}
				REAL_COUNTER += 1
			}
			curIter = 0
		} else {
			err := STATE_MAP.Backward(profit, timeListPTR, pathPTR, tasks)
			if err != nil {
				return 0, nil, err
			}
		}
		REAL_COUNTER += 1
	}

	return maxProfit, &maxProfitPath, nil
}

func (states *StateMap) Forward(tasks *[]Task, curIter int, maxIteration int) (profit float64, time *[]int64, path *[]int64) {
	startTime := int64(0)
	curPath := []int64{}
	timeList := []int64{}
	curProfit := float64(0)

	var action int64
	if TryExplore(curIter, maxIteration) {
		action = states.MinTrialAt(startTime, &curPath, tasks)
	} else {
		action, _ = states.MaxQValAt(startTime, &curPath, tasks)
	}
	for action != int64(-1) {
		timeList = append(timeList, startTime)
		curPath = append(curPath, action)
		curProfit += (*tasks)[int(action)-1].GetProfit(startTime)
		(*(states.Trial[int(startTime)]))[action] += 1
		startTime += (*tasks)[int(action)-1].duration

		if TryExplore(curIter, maxIteration) {
			action = states.MinTrialAt(startTime, &curPath, tasks)
		} else {
			action, _ = states.MaxQValAt(startTime, &curPath, tasks)
		}
	}
	return curProfit, &timeList, &curPath
}

func (states *StateMap) GuideForward(tasks *[]Task, guide *[]int64) (profit float64, time *[]int64, path *[]int64) {
	startTime := int64(0)
	curPath := []int64{}
	timeList := []int64{}
	curProfit := float64(0)

	for i:=0; i<len(*guide); i++ {
		action := (*guide)[i]
		timeList = append(timeList, startTime)
		curPath = append(curPath, action)
		curProfit += (*tasks)[int(action)-1].GetProfit(startTime)
		(*(states.Trial[int(startTime)]))[action] += 1
		startTime += (*tasks)[int(action)-1].duration
	}
	return curProfit, &timeList, &curPath
}

func (states *StateMap) Backward(profit float64, time *[]int64, path *[]int64, tasks *[]Task) error {
	if len(*time) != len(*path) {
		return errors.New("diff length")
	}
	if len(*time) < 2 {
		return errors.New("invalid iteration with length=" + strconv.Itoa(len(*time)))
	}
	for i:=len(*time)-2;i>=0;i-- {
		if i == len(*time)-2 {
			(*(states.Data[int((*time)[i])]))[(*path)[i]] += lr * (reward + gamma * profit) - lr * (*(states.Data[int((*time)[i])]))[(*path)[i]]
		} else {
			tempPath := (*path)[:i+2]
			maxQIDNextState, nextStateLargestQ := states.MaxQValAt((*time)[i+1], &tempPath, tasks)
			if maxQIDNextState == int64(-1) {
				return errors.New("backward face problem")
			}
			(*(states.Data[int((*time)[i])]))[(*path)[i]] += lr * (reward + gamma * nextStateLargestQ) - lr * (*(states.Data[int((*time)[i])]))[(*path)[i]]
		}
	}
	return nil

}

func TryExplore(CurIter int, MaxIter int) bool {
	if CurIter % 10000 == 0 {
		return true
	}
	if CurIter < MaxIter / 100 {
		return rand.Float64() < 0.05
	} else if CurIter < MaxIter / 6 {
		return rand.Float64() < 0.3
	} else if CurIter < (2 * MaxIter / 5) {
		return rand.Float64() < 0.5
	} else if CurIter < (3 * MaxIter / 5) {
		return rand.Float64() < 0.6
	} else if CurIter < (4 * MaxIter / 5) {
		return rand.Float64() < 0.7
	}
	return rand.Float64() < 0.8
}