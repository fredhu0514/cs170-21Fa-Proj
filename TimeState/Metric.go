package TimeState

type State struct {
	Profit	float64
	Path	*[]int
}

func NewState() *State {
	var statePTR State
	statePTR.Profit = -1.0
	tempPath := []int{}
	statePTR.Path = &tempPath
	return &statePTR
}

type Metric struct {
	Pivots			*[][]State // len(tasks) * SPACE_CONSTANT w/ SPACE_CONSTANT>60 because largest is 60
	LargestProfit	float64
	LargestPath		[]int
}

func NewMetric(tasks *[]Task) *Metric {
	var matrix Metric
	matrix.LargestProfit = 0.0
	matrix.LargestPath = []int{}
	var pivot [][]State
	for i:=0; i<len(*tasks); i++ {
		var timeList []State
		for j:=0; j<SPACE_CONSTANT; j++ {
			timeList = append(timeList, *(NewState()))
		}
		pivot = append(pivot, timeList)
	}
	matrix.Pivots = &pivot
	return &matrix
}

func (matrix *Metric) Preprocessing(tasks *[]Task) *Metric {
	for i:=0; i<len(*tasks); i++ {
		(*matrix.Pivots)[i][0].Profit = (*tasks)[i].GetProfit(int64(0))
		tempPath := []int{i}
		(*matrix.Pivots)[i][0].Path = &tempPath
	}
	return matrix
}