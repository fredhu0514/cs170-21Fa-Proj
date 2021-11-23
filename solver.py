# 3.6.9

from parse import read_input_file, write_output_file
import logging

log = "max-profit.log"
logging.basicConfig(
    filename=log,
    level=logging.DEBUG,
    format='%(asctime)s %(message)s',
    datefmt='%d/%m/%Y %H:%M:%S',
    )

def dfs(tasks):
    N = len(tasks)
    count = 0
    max_path = []
    max_profit = 0
    stack = []
    for i in range(N, 0, -1):
        stack.append((0, 0, [i]))
    while len(stack) > 0:
        prev_profit, cur_time, cur_path = stack.pop()
        end_time = tasks[cur_path[-1]-1].duration + cur_time
        if end_time > 1440: # Time exceeds
            if count % 100000 == 0:
                print(str(count))
                logging.info("Iteration: " + str(count))
            count += 1
            continue
        cur_profit = tasks[cur_path[-1]-1].get_profit(cur_time) + prev_profit
        for i in range(1, N + 1):
            if i not in cur_path:
                stack.append((cur_profit, end_time, cur_path + [i]))
        if cur_profit > max_profit:
            max_profit = cur_profit
            max_path = cur_path
            logging.info("New max profit " + str(max_profit))
            write_output_file('./output/' + str(count) + "-" + str(cur_profit) + '.out', max_path)
            logging.info('Solution in ./output/' + str(count) + "-" + str(cur_profit) + '.out')
        if count % 100000 == 0:
            print(str(count))
            logging.info("Iteration: " + str(count))
        count += 1
    return max_path



def get_total_benefit(tasks, solution):
    curTime = 0
    total_profit = 0
    for s in solution:
        total_profit += tasks[s - 1].get_profit(curTime)
        curTime += tasks[s - 1].duration
    return total_profit, curTime

def show_total_benefit(tasks, solution):
    curTime = 0
    total_profit = 0
    for s in solution:
        total_profit += tasks[s-1].get_profit(curTime)
        curTime += tasks[s-1].duration
    logging.info(f"TOTAL TIME " + str(curTime))
    logging.info(f"TOTAL PROFIT " + str(total_profit))

if __name__ == '__main__':
    tasks = read_input_file('./test/100.in')
    tasks.sort(key=lambda x: x.task_id)
    output = dfs(tasks)
    show_total_benefit(read_input_file('./test/100.in'), output)
    write_output_file('./test/100.out', output)
