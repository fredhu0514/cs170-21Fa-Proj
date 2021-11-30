package TimeState

import (
	"bufio"
	"errors"
	"io"
	"os"
	"sort"
	"strconv"
	"strings"
)

func ReadInputFile(filepath string)  (*[]Task, error) {
	var taskList []Task

	// Get the first line number
	file, err := os.Open(filepath)
	if err != nil {
		return nil, errors.New("file DNE")
	}
	reader := bufio.NewReader(file)
	taskNumLine, err := reader.ReadString('\n')
	if err != nil {
		return nil, errors.New("cannot read line")
	}
	taskNumLineList := strings.Fields(taskNumLine)
	taskLength, err := strconv.ParseInt(taskNumLineList[0], 10, 64)
	if err != nil {
		return nil, errors.New("cannot parse total task amount")
	}

	// Parse the rest things
	for  {
		taskString, errEOF := reader.ReadString('\n')
		taskInput := strings.Fields(taskString)
		if len(taskInput) == 0 {
			break
		}
		if len(taskInput) != 4 {
			return nil, errors.New("invalid input length")
		}
		var id, deadline, duration int64
		id, err = strconv.ParseInt(taskInput[0], 10, 64)
		if err != nil {
			return nil, errors.New("invalid input id type")
		}
		deadline, err = strconv.ParseInt(taskInput[1], 10, 64)
		if err != nil {
			return nil, errors.New("invalid input ddl type")
		}
		duration, err = strconv.ParseInt(taskInput[2], 10, 64)
		if err != nil {
			return nil, errors.New("invalid input duration type")
		}
		var benefit float64
		benefit, err = strconv.ParseFloat(taskInput[3], 64)
		if err != nil {
			return nil, errors.New("invalid input benefit type")
		}
		taskList = append(taskList, NewTask(id, deadline, duration, benefit))

		if errEOF == io.EOF {
			break
		}
	}

	// Compare preset length and real length
	if taskLength != int64(len(taskList)) {
		return nil, errors.New("predict length does not match real length")
	}

	// Sort the task list by ID
	sort.Sort(ByID(taskList))

	return &taskList, nil
}

func ReadOutputFile(filepath string)  (*[]int64, error) {
	solution := []int64{}

	// Get the first line number
	file, err := os.Open(filepath)
	if err != nil {
		return nil, errors.New("file DNE")
	}
	reader := bufio.NewReader(file)

	// Parse the rest things
	for  {
		taskIDStr, errEOF := reader.ReadString('\n')
		if errEOF == io.EOF {
			break
		}
		taskIDList := strings.Fields(taskIDStr)
		taskID, err := strconv.ParseInt(taskIDList[0], 10, 64)
		if err != nil {
			return nil, errors.New("cannot parse total task amount")
		}
		solution = append(solution, taskID)
	}
	return &solution, nil
}

func WriteOutputFile(filepath string, taskIDs []int64) error {
	f, err := os.Create(filepath)
	if err != nil {
		return errors.New("cannot create file: " + filepath)
	}
	defer f.Close()
	writer := bufio.NewWriter(f)
	for i := range taskIDs {
		_, err := writer.WriteString(strconv.Itoa(int(taskIDs[i])) + "\n")
		if err != nil {
			return errors.New("cannot write to the buffer")
		}
	}
	err = writer.Flush()
	if err != nil {
		errors.New("cannot write the buffer to the file")
	}
	return nil
}