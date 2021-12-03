from parse import read_input_file, write_output_file, read_output_file

def get_total_benefit(tasks, solution):
    curTime = 0
    total_profit = 0
    tasks.sort(key=lambda x : x.task_id)
    for s in solution:
        total_profit += tasks[s - 1].get_profit(curTime)
        curTime += tasks[s - 1].duration
    return total_profit


if __name__ == '__main__':
    import os
    print(os.getcwd())
    tasks = read_input_file('./inputs/small/small-73.in')
    print(tasks)
    output = read_output_file('./testOP/small/small-73.out')
    print(output)
    profit = get_total_benefit(tasks, output)
    print(profit)

# python3 python-test/solver.py