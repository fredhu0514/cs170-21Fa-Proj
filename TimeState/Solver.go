package TimeState

func Solver(tasks *[]Task) *[]int64 {
	rawOutputPTR := Iteration(tasks)
	var output []int64
	for i:=0; i<len(*rawOutputPTR);i++ {
		output = append(output, int64((*rawOutputPTR)[i] + 1))
	}
	return &output
}
