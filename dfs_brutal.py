from parse import read_input_file, write_output_file

def dfs(tasks):
    N = len(tasks)
    count = 0
    max_path = []
    max_profit = 0
    stack = []
    for i in range(N, 0, -1):
        stack.append([i])
    while len(stack) > 0:
        cur_path = stack.pop()
        for i in range(1, N + 1):
            if i not in cur_path:
                stack.append(cur_path + [i])
        cur_profit = get_total_benefit(tasks, cur_path)
        if cur_profit > max_profit:
            max_profit = cur_profit
            max_path = cur_path
            print("New max profit " + str(max_profit))
            write_output_file('./output/' + str(count) + '.out', max_path)
            print('Solution in ./output/' + str(count) + '.out')
        if count % 100000 == 0:
            print("Iteration: " + str(count))
        count += 1
    return max_path



def get_total_benefit(tasks, solution):
    curTime = 0
    total_profit = 0
    for s in solution:
        total_profit += tasks[s - 1].get_profit(curTime)
        curTime += tasks[s - 1].duration
    return total_profit

def show_total_benefit(tasks, solution):
    curTime = 0
    total_profit = 0
    for s in solution:
        total_profit += tasks[s-1].get_profit(curTime)
        curTime += tasks[s-1].duration
    print(f"TOTAL TIME " + str(curTime))
    print(f"TOTAL PROFIT " + str(total_profit))

if __name__ == '__main__':
    tasks = read_input_file('./test/100.in')
    tasks.sort(key=lambda x: x.task_id)
    output = dfs(tasks)
    show_total_benefit(read_input_file('./test/100.in'), output)
    write_output_file('./test/100.out', output)