package TimeState

import "errors"

func AllProfit(tasks *[]Task, output *[]int64) (float64, error) {
    realTasks := *tasks
    outputs := *output
    curTime := 0
    profit := 0.0

    for i := range outputs {
        id := int(outputs[i]) - 1
        profit += realTasks[id].GetRealProfit(int64(curTime))
        curTime += int(realTasks[id].duration)
    }

    if curTime > 1440 {
        return 0, errors.New("TIME EXCEEDS")
    }
    return profit, nil
}