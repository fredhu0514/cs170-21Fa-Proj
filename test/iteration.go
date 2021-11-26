package test

import "errors"

var CurTransition = []TransitionUnit{}

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

//	dummyNode := NewState(0, []int64{}, 0, []IDqValTrial{}, map[int64]IDqValTrial, false)
func forwardTransition(dummyState State, taskList []Task) error {
	curState := dummyState
	prevIndex := ""
	for !curState.isFinal {
		// if exploring or exploitation
		var action int64
		if tryExploitation() {
			action = curState.SmallestTrialID()
		} else {
			action = curState.LargestQValID()
		}
		nextState, newIndex, err := curState.NextState(action, taskList)
		if err != nil {
			return errors.New("errors during forward transition")
		}
		// Update current path
		CurTransition = append(CurTransition, NewTransitionUnit(prevIndex, newIndex, action))
		// Update current state
		curState = nextState
		// Update prev index
		prevIndex = newIndex
	}
	return nil
}

//func backwardTransition() error {
//	// Assert the transition length != 0
//	if len(CurTransition) < 1 {
//		return errors.New("error in backward transition or no transition data")
//	}
//	// for loop though out the value update
//	for i:=len(CurTransition)-1; i>=0; i-- {
//
//	}
//}

func tryExploitation() bool {
	return true
}