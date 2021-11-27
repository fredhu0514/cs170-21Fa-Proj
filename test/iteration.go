package test

import (
	"errors"
	"fmt"
	"strconv"
)

var lr float64 = 0.001
var reward float64 = 0.0
var gamma float64 = 0.8

type TransitionUnit struct {
	iState		string // Start state
	tState		string // End state
	action		int64 // Transition action
}

func NewTransitionUnit(i string,  t string, a int64) (unit TransitionUnit) {
	unit.iState = i
	unit.tState = t
	unit.action = a
	return unit
}

func Train(MaxIter int, PTRStateMap *map[string]State, PTRCurTransition *[]TransitionUnit,
	PTRTaskList *[]Task) (float64, []int64, error) {
	fmt.Println("A1")
	// Init current iteration index
	var CurIter int = 0
	// Init Max Profit and Max Profit Path
	var MaxProfitEver []float64 = []float64{0}
	var MaxProfitEverList []int64 = []int64{}
	// Init dummy node
	nullPath := []int64{}
	qVals := []IDqValTrial{}
	updateMap := map[int64]IDqValTrial{}
	for i := range *PTRTaskList {
		obj := NewIDqValTrial((*PTRTaskList)[i].task_id, float64(0), 0)
		qVals = append(qVals, obj)
		updateMap[(*PTRTaskList)[i].task_id] = obj
	}
	(*PTRStateMap)[""] = NewState(float64(0), nullPath, int64(0), qVals, updateMap, false)
	for CurIter < MaxIter  {
		err := Iteration(&MaxProfitEver, &MaxProfitEverList, CurIter, MaxIter,
			PTRStateMap, PTRCurTransition, PTRTaskList)
		if err != nil {
			return 0, nil, err
		}
		CurIter += 1
	}
	return MaxProfitEver[0], MaxProfitEverList, nil
}

func Iteration(MaxProfitEver *[]float64, MaxProfitEverList *[]int64, CurIter int, MaxIter int,
	PTRStateMap *map[string]State, PTRCurTransition *[]TransitionUnit, PTRTaskList *[]Task) error {

	fmt.Println("B1"+ "-" + strconv.Itoa(CurIter))
	dummyStart, ok := (*PTRStateMap)[""]
	if !ok {
		return errors.New("dummy node DNE!")
	}

	//  Forwards
	err := forwardTransition(PTRStateMap, PTRCurTransition, CurIter, MaxIter, dummyStart, PTRTaskList)
	if err != nil {
		return err
	}
	fmt.Println(len(*PTRStateMap))
	fmt.Println("B2"+ "-" + strconv.Itoa(CurIter))

	// Backward
	err = backwardTransition(MaxProfitEver, MaxProfitEverList, PTRStateMap, PTRCurTransition)
	if err != nil {
		return err
	}

	// Reassign the global variable CurTransition
	*PTRCurTransition = []TransitionUnit{}
	// TODO: DEBUG
	fmt.Println(len(*PTRStateMap))
	fmt.Println("B3"+ "-" + strconv.Itoa(CurIter))
	return nil
}

func forwardTransition(PTRStateMap *map[string]State, PTRCurTransition *[]TransitionUnit, CurIter int,
	MaxIter int, dummyState State, PTRTaskList *[]Task) error {
	fmt.Println("C1" + "-" + strconv.Itoa(CurIter))
	curState := dummyState
	prevIndex := ""
	for !curState.isFinal {
		// if exploring or exploitation
		var action int64
		if tryExploitation(CurIter, MaxIter) {
			action = curState.SmallestTrialID()
		} else {
			action = curState.LargestQValID()
		}
		fmt.Print(strconv.Itoa(int(action)) + "-")
		nextState, newIndex, err := curState.NextState(action, PTRTaskList, PTRStateMap)
		if err != nil {
			return errors.New("errors during forward transition")
		}
		// Update current path
		*PTRCurTransition = append(*PTRCurTransition, NewTransitionUnit(prevIndex, newIndex, action))
		// Update current state
		curState = nextState
		// Update prev index
		prevIndex = newIndex
	}
	fmt.Print("\n")
	fmt.Println(len(*PTRStateMap))
	fmt.Println(curState)
	fmt.Println("C2" + "-" + strconv.Itoa(CurIter))
	return nil
}

func backwardTransition(MaxProfitEver *[]float64, PTRMaxProfitEverList *[]int64, PTRStateMap *map[string]State,
	PTRCurTransition *[]TransitionUnit) error {

	fmt.Println("D1" + "---")
	fmt.Println(*MaxProfitEver)

	CurTransition := *PTRCurTransition
	// Assert the transition length != 0
	if len(CurTransition) < 1 {
		return errors.New("error in backward transition or no transition data")
	}
	// Assert the last state must be end
	lastIndex := len(CurTransition)-1
	finalState, ok := (*PTRStateMap)[CurTransition[lastIndex].tState]
	if !ok {
		return errors.New("cannot get the last state from the state map")
	}
	if !finalState.isFinal {
		return errors.New("last but not final state")
	}
	// for loop throughout the value update
	for i:=lastIndex; i>=0; i-- {
		curTransUnit := CurTransition[i]
		iStateID := curTransUnit.iState
		tStateID := curTransUnit.tState
		action := curTransUnit.action

		iState, ok := (*PTRStateMap)[iStateID]
		if !ok {
			return errors.New("DNE s.t. state? " + iStateID)
		}
		bjIDqValTrial, ok := iState.updateMap[action]
		if !ok {
			return errors.New("Has no qVal? " + iStateID)
		}
		tState, ok := (*PTRStateMap)[tStateID]
		if !ok {
			return errors.New("DNE s.t. terminal state? " + tStateID)
		}
		if tState.isFinal {
			var amplifier float64 = 1
			profit := tState.curProfit
			if profit > (*MaxProfitEver)[0]  {
				(*MaxProfitEver)[0] = profit
				*PTRMaxProfitEverList = tState.curPath
				amplifier = 1
			}
			bjIDqValTrial.qVal = (1 - lr) * bjIDqValTrial.qVal + lr * (reward + gamma * profit * amplifier)
		} else {
			maxTerminalStateQVal, err := stateMaxValue(tState)
			if err != nil {
				return err
			}
			bjIDqValTrial.qVal = (1 - lr) * bjIDqValTrial.qVal + lr * (reward + gamma * maxTerminalStateQVal)
		}
	}
	fmt.Println((*PTRStateMap)[""].updateMap)
	fmt.Println("D2" + "---")
	return nil
}

func stateMaxValue(state State) (float64, error) {
	if state.isFinal {
		return 0, errors.New("should not be final state")
	} else {
		maxVal := state.qVals[0].qVal
		for i := range state.qVals {
			if state.qVals[i].qVal > maxVal {
				maxVal = state.qVals[i].qVal
			}
		}
		return maxVal, nil
	}
}

func tryExploitation(CurIter int, MaxIter int) bool {
	return true // rand.Float64() > 0 // < float64(1 - CurIter / MaxIter)
}