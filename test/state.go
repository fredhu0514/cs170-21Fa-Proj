package test

import (
	"errors"
	"fmt"
	"strconv"
)

var MaxTime = int64(1440)

type State struct {
	curProfit	float64
	curPath		[]int64
	curTime		int64
	qVals		[]IDqValTrial
	updateMap 	map[int64]IDqValTrial
	isFinal		bool
}

type IDqValTrial struct {
	id			int64
	qVal		float64
	trial		int
}

// Get largest QVal id
func (state *State) LargestQValID() int64 {
	if state.isFinal {
		return int64(-1)
	}
	maxQVal := state.qVals[0].qVal
	maxID := state.qVals[0].id
	for i:=0; i<len(state.qVals); i++{
		if state.qVals[i].qVal > maxQVal {
			maxID = state.qVals[i].id
		}
	}
	return maxID
}

// Get smallest trial id
func (state *State) SmallestTrialID() int64 {
	if state.isFinal {
		return int64(-1)
	}
	minTrial := state.qVals[0].qVal
	minID := state.qVals[0].id
	for i:=0; i<len(state.qVals); i++{
		if state.qVals[i].qVal > minTrial {
			minID = state.qVals[i].id
		}
	}
	return minID
}


func NewIDqValTrial(id int64, qVal float64, trial int) IDqValTrial {
	var obj IDqValTrial
	obj.id = id
	obj.qVal = qVal
	obj.trial = trial
	return obj
}

func NewState(curProfit float64, curPath []int64, curTime int64, qVals []IDqValTrial, updateMap map[int64]IDqValTrial, isFinal bool) (state State) {
	state.curProfit = curProfit
	state.curPath = curPath
	state.curTime = curTime
	state.qVals = qVals
	state.updateMap = updateMap
	state.isFinal = isFinal
	return state
}

func (prevState *State) NextState(action int64, PTRTaskList *[]Task, PTRStateMap *map[string]State) (state State, index string, err error) {
	fmt.Println("E1----")
	// check if is derived from last step
	wantedIDqValTrial, ok := prevState.updateMap[action]
	if !ok {
		return state, "", errors.New("such move is not derived from last step")
	}
	fmt.Println(prevState.updateMap[action])

	// Check if such move really valid
	curTime := prevState.curTime + (*PTRTaskList)[int(action)-1].duration
	if curTime > MaxTime {
		return state, "", errors.New("invalid move, and should not exits")
	}
	state.curTime = curTime // update cur time if valid

	// Update last time such move
	wantedIDqValTrial.trial += 1
	fmt.Println("E2----")
	fmt.Println(prevState.updateMap[action])
	fmt.Println("E3----")
	// Update cur path
	state.curPath = append(prevState.curPath, action)

	// Update index list
	s := ""
	for k:=0; k<len(state.curPath); k++ {
		s = s + strconv.Itoa(int(state.curPath[k])) + "-"
	}

	// Check if we have spanned this state before
	newState, ok := (*PTRStateMap)[s]
	if ok {
		return newState, s, nil
	}

	// Get such task
	actionTask := (*PTRTaskList)[int(action)-1]

	// Update cur profit
	state.curProfit = prevState.curProfit + actionTask.GetProfit(prevState.curTime)

	// Create new qVals and updateMap
	qVals := []IDqValTrial{}
	updateMap := map[int64]IDqValTrial{}
	for i:=0; i<len(prevState.qVals); i++ {
		id := prevState.qVals[i].id // All valid moves must be a subset of last state
		if id != action {
			if !((*PTRTaskList)[int(id)-1].duration + curTime > MaxTime) { // check if the time passed max time
				curQValObj := NewIDqValTrial(id, float64(0), 0) // Set such an object
				qVals = append(qVals, curQValObj) // Update the qValue
				updateMap[id] = curQValObj // update the update map
			}
		}
	}

	// Update qVal list
	state.qVals = qVals
	state.updateMap = updateMap

	// Check if it is the final state
	if len(qVals) == 0 {
		state.isFinal = true
	} else {
		state.isFinal = false
	}

	// Add this state to the global map
	(*PTRStateMap)[s] = state

	return state, s, nil
}
