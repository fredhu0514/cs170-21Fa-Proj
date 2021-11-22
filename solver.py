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
    for _ in range(N):
        cur_task_quantity = len(tasks)
        profit_over_duration_list = np.zeros(cur_task_quantity)
        for i in range(cur_task_quantity):
            profit_over_duration_list[i] = tasks[i].get_benefit_per_timestamp(curTime)
        greedy_index = np.argmax(profit_over_duration_list)
        # Return if no valid moves
        if profit_over_duration_list[greedy_index] < 0:
            return path
        # Pop this task
        greedy_task = tasks.pop(greedy_index)
        # Update path
        path.append(greedy_task.get_task_id())
        # Update curTime
        curTime += greedy_task.get_duration()

def get_total_benefit(tasks, solution):
    mapp = {}
    curTime = 0
    total_profit = 0
    for task in tasks:
        mapp[task.task_id] = task
    for s in solution:
        total_profit += mapp[s].get_profit(curTime)
        curTime += mapp[s].get_duration()
    print(f"TOTAL TIME {curTime}")
    print(f"TOTAL PROFIT {total_profit}")


if __name__ == '__main__':
    tasks = read_input_file('./test/100.in')
    output = solve(tasks)
    get_total_benefit(read_input_file('./test/100.in'), output)
    write_output_file('./test/100.out', output)