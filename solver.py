from parse import read_input_file, write_output_file

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
        profit_over_duration_list = [0] * cur_task_quantity
        greedy_index = 0
        for i in range(cur_task_quantity):
            profit_over_duration_list[i] = tasks[i].get_decay_profit(curTime)
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

def get_total_benefit(tasks, solution):
    curTime = 0
    total_profit = 0
    tasks.sort(key=lambda x : x.task_id)
    for s in solution:
        total_profit += tasks[s - 1].get_profit(curTime)
        curTime += tasks[s - 1].duration
    return total_profit


if __name__ == '__main__':
    tasks = read_input_file('./test/100/sample1.in')
    output = solve(tasks)
    get_total_benefit(read_input_file('./test/100.in'), output)
    write_output_file('./test/100.out', output)