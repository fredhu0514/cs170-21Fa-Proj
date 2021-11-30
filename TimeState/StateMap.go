package TimeState

import (
	"math"
)

type StateMap struct {
	Data	[]*map[int64]float64
	Trial	[]*map[int64]int
}

func NewStateMap(tasks *[]Task) *StateMap {
	STATE_MAP := StateMap{}
	STATE_MAP.Data = []*map[int64]float64{}
	STATE_MAP.Trial = []*map[int64]int{}
	for i:=0; i<1440; i++ {
		qval := map[int64]float64{}
		trial := map[int64]int{}
		for j:=0;j<len(*tasks);j++ {
			qval[(*tasks)[j].task_id] = 0.0
			trial[(*tasks)[j].task_id] = 0
		}
		STATE_MAP.Data = append(STATE_MAP.Data, &qval)
		STATE_MAP.Trial = append(STATE_MAP.Trial, &trial)
	}
	return &STATE_MAP
}

func (states *StateMap) MaxQValAt(curTime int64, path *[]int64, tasks *[]Task) (int64, float64) {
	curState:= (*states).Data[int(curTime)]
	maxID := int64(-1)
	maxQVal := math.Inf(-1)
	for id, qVal := range *curState {
		flag := true
		// duration exceeds 1440?
		if (*tasks)[int(id)-1].duration + curTime >= int64(1440) {
			flag = false
		}

		// not in the path
		if flag {
			for i:=0; i<len(*path); i++ {
				if (*path)[i] == id {
					flag = false
					break
				}
			}
			if flag {
				if qVal > maxQVal {
					maxID = id
					maxQVal = qVal
				}
				if qVal == maxQVal {
					if id < maxID {
						maxID = id
					}
				}
			}
		}
	}
	return maxID, maxQVal
}

func (states *StateMap) MinTrialAt(curTime int64, path *[]int64, tasks *[]Task) (int64) {
	curState:= (*states).Trial[curTime]
	minID := int64(-1)
	minTrial := math.Inf(1)
	for id, trial := range *curState {
		flag := true
		// duration exceeds 1440?
		if (*tasks)[int(id)-1].duration + curTime >= int64(1440) {
			flag = false
		}
		if flag {
			// not in the path
			for i:=0; i<len(*path); i++ {
				if (*path)[i] == id {
					flag = false
					break
				}
			}
			if flag {
				if minTrial > float64(trial) {
					minID = id
					minTrial = float64(trial)
				}
				if minTrial == float64(trial) {
					if id > minID {
						minID = id
					}
				}
			}
		}
	}
	return minID
}

