from parse import read_input_file, write_output_file
import numpy as np

def solve(tasks):
    """
    Args:
        tasks: list[Task], list of igloos to polish
    Returns:
        output: list of igloos in order of polishing  
    """
    N = len(tasks)
    path = []
    curTime = 0
    total_profit = 0
    while True:
        cur_task_quantity = len(tasks)
        profit_over_duration_list = np.zeros(cur_task_quantity)
        profit_list = [0] * cur_task_quantity
        end_time_list = [0] * cur_task_quantity
        for i in range(cur_task_quantity):
            profit_over_duration_list[i], profit_list[i], end_time_list[i] = tasks[i].get_benefit_per_timestamp(curTime)
        greedy_index = np.argmax(profit_over_duration_list)
        # Return if no valid moves
        if profit_over_duration_list[greedy_index] < 0:
            print(total_profit)
            return path
        # Update path
        path.append(tasks[greedy_index].get_task_id())
        # Update total profit
        total_profit += profit_list[greedy_index]
        # Update curTime
        curTime = end_time_list[greedy_index]
        # Pop this task
        tasks.pop(greedy_index)


# Here's an example of how to run your solver.
if __name__ == '__main__':
    tasks = read_input_file('./test/100.in')
    output = solve(tasks)
    write_output_file('./test/100.out', output)