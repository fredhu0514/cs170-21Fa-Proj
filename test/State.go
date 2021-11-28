package test

import (
	"errors"
	"strconv"
)

var MaxTime = int64(1440)

type State struct {
	curProfit	float64
	curPath		[]int64
	curTime		int64
	qVals 		map[int64]*IDqValTrial
	isFinal		bool
}

type IDqValTrial struct {
	qVal		float64
	trial		int
}

func NewIDqValTrial(qVal float64, trial int) *IDqValTrial {
	var obj IDqValTrial
	obj.qVal = qVal
	obj.trial = trial
	return &obj
}

func NewState(curProfit float64, curPath []int64, curTime int64,
	qVals map[int64]*IDqValTrial, isFinal bool) *State {
	var state State
	state.curProfit = curProfit
	state.curPath = curPath
	state.curTime = curTime
	state.qVals = qVals
	state.isFinal = isFinal
	return &state
}

func (prevState *State) NextState(action int64, PTRTaskList *[]Task, PTRStateMap *map[string]*State) (statePTR *State, index string, err error) {
	var state State
	// check if is derived from last step
	PTRwantedIDqValTrial, ok := prevState.qVals[action]
	if !ok {
		return nil, "", errors.New("such move is not derived from last step")
	}

	// Check if such move really valid
	curTime := prevState.curTime + (*PTRTaskList)[int(action)-1].duration
	if curTime > MaxTime {
		return nil, "", errors.New("invalid move, and should not exits")
	}
	state.curTime = curTime // update cur time if valid

	// Update last time such move
	(*PTRwantedIDqValTrial).trial += 1
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
	qVals := map[int64]*IDqValTrial{}
	for id, _ := range prevState.qVals {
		// All valid moves must be a subset of last state
		if id != action {
			if !((*PTRTaskList)[int(id)-1].duration + curTime > MaxTime) { // check if the time passed max time
				qVals[id] = NewIDqValTrial(float64(0), 0) // Set such an object and update the update map
			}
		}
	}

	// Update qVal list
	state.qVals = qVals

	// Check if it is the final state
	if len(qVals) == 0 {
		state.isFinal = true
	} else {
		state.isFinal = false
	}

	// Add this state to the global map
	(*PTRStateMap)[s] = &state

	return &state, s, nil
}

// Get smallest trial id
func (state *State) SmallestTrialID() (int64) {
	var minID int64
	var minTrial int
	for k, v := range state.qVals {
		minID = k
		minTrial = (*v).trial
		break
	}
	for k, v := range state.qVals {
		if (*v).trial < minTrial {
			minID = k
			minTrial = (*v).trial
		} else if (*v).trial == minTrial { // for deterministic under random seed
			if k < minID {
				minID = k
			}
		}
	}
	return minID
}

// Get largest QVal id
func (state *State) LargestQValID() (int64) {
	var maxID int64
	var maxQVal float64
	for k, v := range state.qVals {
		maxID = k
		maxQVal = (*v).qVal
		break
	}
	for k, v := range state.qVals {
		if (*v).qVal > maxQVal {
			maxID = k
			maxQVal = (*v).qVal
		} else if (*v).qVal == maxQVal { // for deterministic under random seed
			if k > maxID {
				maxID = k
			}
		}
	}
	return maxID
}

// Get largest QVal
func (state *State) MaxQVal() (float64, error) {
	if state.isFinal {
		return 0, errors.New("is final state")
	}
	var maxQVal float64
	for _, v := range state.qVals {
		maxQVal = (*v).qVal
		break
	}
	for _, v := range state.qVals {
		if (*v).qVal > maxQVal {
			maxQVal = (*v).qVal
		}
	}
	return maxQVal, nil
}