package TimeState

var Time_Length int = 120

type State struct {
	Profit	float64
	Path	[]int
}

func EmptyState() *State {
	var state State
	state.Profit = -1.0
	state.Path = []int{}
	return &state
}

type Metric struct {
	Matrix		[][]State
	MaxProfit	float64
	MaxPath		[]int
}

func NewMetric(tasks *[]Task) *Metric {
	var metric Metric
	metric.MaxPath = []int{}
	metric.MaxProfit = -5.0
	var mat [][]State
	for i:=0; i<Time_Length; i++{
		temp := []State{}
		for j:=0; j<len(*tasks); j++ {
			tempState := EmptyState()
			temp = append(temp, *tempState)
		}
		mat = append(mat, temp)
	}
	metric.Matrix = mat
	return &metric
}

func InitMetric(tasks *[]Task) *Metric {
	metric := NewMetric(tasks)
	for j:=0; j<len(*tasks); j++ {
		metric.Matrix[0][j].Profit = (*tasks)[j].GetProfit(int64(0))
		metric.Matrix[0][j].Path = append(metric.Matrix[0][j].Path, j)
	}
	return metric
}