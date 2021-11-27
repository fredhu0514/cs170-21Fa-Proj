package test

import (
	"errors"
	"fmt"
	"math/rand"
)

type TransitionUnit struct {
	iState		string // Start state
	tState		string // End state
	action		int64 // Transition action
}

func NewTransitionUnit(i string, t string, a int64) (unit TransitionUnit) {
	unit.iState = i
	unit.tState = t
	unit.action = a
	return unit
}

func Iteration(MaxProfitEver *[]float64, MaxProfitEverList *[]int64, CurIter int, MaxIter int,
	PTRStateMap *map[string]*State, PTRCurTransition *[]TransitionUnit, PTRTaskList *[]Task) error {

	dummyStart, ok := (*PTRStateMap)[""]
	if !ok {
		return errors.New("dummy node DNE!")
	}

	//  Forwards
	err := forwardTransition(PTRStateMap, PTRCurTransition, CurIter, MaxIter, dummyStart, PTRTaskList)
	if err != nil {
		return err
	}

	// Backward
	err = backwardTransition(MaxProfitEver, MaxProfitEverList, PTRStateMap, PTRCurTransition, CurIter)
	if err != nil {
		return err
	}

	// Reassign the global variable CurTransition
	*PTRCurTransition = []TransitionUnit{}
	return nil
}

func forwardTransition(PTRStateMap *map[string]*State, PTRCurTransition *[]TransitionUnit, CurIter int,
	MaxIter int, dummyState *State, PTRTaskList *[]Task) error {
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
		// TODO: DEBUG
		// fmt.Print(strconv.Itoa(int(action)) + "-")
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
	// TODO: DEBUG
	// fmt.Print("\n")
	return nil
}

func backwardTransition(MaxProfitEver *[]float64, PTRMaxProfitEverList *[]int64, PTRStateMap *map[string]*State,
	PTRCurTransition *[]TransitionUnit, CurIter int) error {

	CurTransition := *PTRCurTransition
	// Assert the transition length != 0
	if len(CurTransition) < 1 {
		return errors.New("error in backward transition or no transition data")
	}
	// Assert the last state must be end
	lastIndex := len(CurTransition)-1
	finalStatePTR, ok := (*PTRStateMap)[CurTransition[lastIndex].tState]
	if !ok {
		return errors.New("cannot get the last state from the state map")
	}
	if !finalStatePTR.isFinal {
		return errors.New("last but not final state")
	}
	// for loop throughout the value update
	for i:=lastIndex; i>=0; i-- {
		curTransUnit := CurTransition[i]
		iStateID := curTransUnit.iState
		tStateID := curTransUnit.tState
		action := curTransUnit.action

		iStatePTR, ok := (*PTRStateMap)[iStateID]
		if !ok {
			return errors.New("DNE s.t. state? " + iStateID)
		}
		bjIDqValTrialPTR, ok := iStatePTR.qVals[action]
		if !ok {
			return errors.New("Has no qVal? " + iStateID)
		}
		tStatePTR, ok := (*PTRStateMap)[tStateID]
		if !ok {
			return errors.New("DNE s.t. terminal state? " + tStateID)
		}
		if tStatePTR.isFinal {
			var amplifier float64 = 1
			profit := tStatePTR.curProfit
			if profit > (*MaxProfitEver)[0]  {
				// TODO: HYPER PARAMETER DOWN
				if profit - (*MaxProfitEver)[0] > 1800 {
					amplifier = 5
				} else {
					amplifier = 50
				}
				// TODO: HYPER PARAMETER UP
				(*MaxProfitEver)[0] = profit
				*PTRMaxProfitEverList = tStatePTR.curPath
				fmt.Print("\n")
				fmt.Print(CurIter)
				fmt.Print(", ")
				fmt.Print((*MaxProfitEver)[0])
				fmt.Print(", ")
				fmt.Print(len(*PTRStateMap))
				fmt.Print("\n")
			}
			iStatePTR.qVals[action].qVal += lr * (reward + gamma * profit * amplifier) - lr * bjIDqValTrialPTR.qVal
		} else {
			maxTerminalStateQVal, err := tStatePTR.MaxQVal()
			if err != nil {
				return err
			}
			iStatePTR.qVals[action].qVal += lr * (reward + gamma * maxTerminalStateQVal) - lr * bjIDqValTrialPTR.qVal
		}
	}
	return nil
}

func tryExploitation(CurIter int, MaxIter int) bool {
	return rand.Float64() < float64(1 - CurIter / MaxIter)
}