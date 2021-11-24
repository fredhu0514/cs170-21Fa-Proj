import random
import math
from parse import read_input_file, write_output_file


ITER_PORTION = 0.3 # tasks amount * ITER_PORTION = iterations per case
THRESHOLD_FUNC = "0 if iteration == max_iteration else 0.05"
RANDOM_SEED = 20211123
random.seed(RANDOM_SEED)

def exploit(iteration, max_iteration) -> bool:
    iteration = iteration + 1
    threshold = 0 if iteration == max_iteration else 0.05
    return random.random() < threshold

def solve(tasks):
    MAX_ITERATION = int(len(tasks) * ITER_PORTION)
    max_profit = 0
    max_path = []
    for iter in range(MAX_ITERATION):
        task_list = tasks.copy()
        curTime = 0
        cur_path = []
        for _ in range(len(tasks)):
            cur_task_quantity = len(task_list)
            if cur_task_quantity == 0:
                break
            greedy_index = 0
            if exploit(iter, MAX_ITERATION):
                # Random trial
                greedy_index = random.randint(0, cur_task_quantity - 1)
                while len(task_list) > 1 and task_list[greedy_index].get_decay_profit(curTime) < 0:
                    task_list.pop(greedy_index) # If the job cannot add now, it cannot be added later. Delete it
                    greedy_index = random.randint(0, len(task_list) - 1)
            else:
                # Greedy decay
                profit_over_duration_list = [0] * cur_task_quantity
                for i in range(cur_task_quantity):
                    profit_over_duration_list[i] = task_list[i].get_decay_profit(curTime)
                    if profit_over_duration_list[greedy_index] < profit_over_duration_list[i]:
                        greedy_index = i
            # Return if no valid moves
            if task_list[greedy_index].get_decay_profit(curTime) < 0:
                cur_profit = get_total_benefit(tasks, cur_path)
                if cur_profit > max_profit:
                    max_profit = cur_profit
                    max_path = cur_path
                break
            # Pop this task
            greedy_task = task_list.pop(greedy_index)
            # Update path
            cur_path.append(greedy_task.task_id)
            # Update curTime
            curTime += greedy_task.duration
    return max_path

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
    get_total_benefit(tasks, output)
    write_output_file('./test-sample1.out', output)