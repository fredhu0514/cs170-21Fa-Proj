package TimeState

import "math"

type StateMap struct {
	Data	[]*map[int64]float64
}

func (states *StateMap) MxQValAt(time int64, tasks *[]Task) error {
	curState:= (*states).Data[time]
	maxID := int64(-1)
	maxQVal := math.Inf(-1)
	for id, qval := range *curState {

	}

}
