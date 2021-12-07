from parse import read_input_file, write_output_file
import numpy as np
import pandas as pd
import pickle

def solve(tasks):
    model = pickle.load(open( "small_svm.sav", "rb" ) )
    """
    Args:
        tasks: list[Task], list of igloos to polish
    Returns:
        output: list of igloos in order of polishing
    """
    N = len(tasks)
    path = []
    curTime = 0
    for _ in range(N):
        cur_task_quantity = len(tasks)
        profit_over_duration_list = [0] * cur_task_quantity
        greedy_index = 0

        result = []
        for j in range(cur_task_quantity):
            curDF = pd.DataFrame(np.array([[curTime, tasks[j].duration, tasks[j].deadline, tasks[j].get_profit(curTime)]]), columns=['cur_time', 'duration', 'ddl', 'cur_profit'])
            pre_result = model.predict(curDF)
            result.append(pre_result[0])
        if sum(result) == 0:
            for i in range(cur_task_quantity):
                profit_over_duration_list[i] = tasks[i].get_benefit_per_timestamp(curTime)
                if profit_over_duration_list[greedy_index] < profit_over_duration_list[i]:
                    greedy_index = i
        else:
            for i in range(cur_task_quantity):
                if result[i] == 0:
                    continue
                profit_over_duration_list[i] = tasks[i].get_benefit_per_timestamp(curTime)
                if profit_over_duration_list[greedy_index] < profit_over_duration_list[i]:
                    greedy_index = i



        # Return if no valid moves
        if profit_over_duration_list[greedy_index] < 0:
            return path
        # Pop this task
        greedy_task = tasks.pop(greedy_index)
        # Update path
        path.append(greedy_task.task_id)
        # Update curTime
        curTime += greedy_task.duration
    return path

def get_total_benefit(tasks, solution):
    curTime = 0
    total_profit = 0
    tasks.sort(key=lambda x : x.task_id)
    for s in solution:
        total_profit += tasks[s - 1].get_profit(curTime)
        curTime += tasks[s - 1].duration
    return total_profit


if __name__ == '__main__':
    tasks = read_input_file('./input/small/small-2.in')
    output = solve(tasks)
    get_total_benefit(read_input_file('./input/small/small-2.in'), output)
    write_output_file('./test.out', output)