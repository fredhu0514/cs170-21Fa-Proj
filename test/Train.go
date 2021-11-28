package test

func Train(MaxIteration int, PTRrawOP *[]int64, PTRTaskList *[]Task) (float64, []int64, error) {
	// Init Max Profit & Max Path
	MAX_PROFIT := []float64{0}
	MAX_PATH := []int64{}

	// Init dummy node
	nullPath := []int64{}
	qVals := map[int64]*IDqValTrial{}
	for i := range *PTRTaskList {
		qVals[(*PTRTaskList)[i].task_id] = NewIDqValTrial(float64(0), 0)
	}
	// Init a State Map
	StateMap := map[string]*State{}
	// Init Cur Transition record  and push it into the StateMap
	CurTransition := []TransitionUnit{}
	StateMap[""] = NewState(float64(0), nullPath, int64(0), qVals, false)

	// Guide Iter
	err := GuideIteration(&MAX_PROFIT, &MAX_PATH, &StateMap, &CurTransition, PTRTaskList, PTRrawOP)
	if err != nil {
		return 0, nil, err
	}

	// Init CurIter and ready for iteration
	CurIter := 0
	for CurIter < MaxIteration  {
		err := Iteration(&MAX_PROFIT, &MAX_PATH, CurIter, MaxIteration,
			&StateMap, &CurTransition, PTRTaskList)
		if err != nil {
			return 0, nil, err
		}
		CurIter += 1
	}
	return MAX_PROFIT[0], MAX_PATH, nil
}
