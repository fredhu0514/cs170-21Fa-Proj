package TimeState

import (
"math"
)

type Task struct {
	task_id 	int64
	deadline	int64
	duration	int64
	benefit		float64
}

// Sort Task Interface
type ByID []Task
func (a ByID) Len() int {
	return len(a)
}
func (a ByID) Less(i, j int) bool {
	return a[i].task_id < a[j].task_id
}
func (a ByID) Swap(i, j int) {
	a[i], a[j] = a[j], a[i]
}

func NewTask(task_id int64, deadline int64, duration int64, benefit float64) (Task) {
	var t Task
	t.task_id = task_id
	t.deadline = deadline
	t.duration = duration
	t.benefit = benefit
	return t
}

func (task *Task) GetProfit(curTime int64) float64 {
	return task.benefit * math.Exp(-0.0170 * math.Max(0, float64(curTime + task.duration - task.deadline)))
}
