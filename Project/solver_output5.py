from parse import read_input_file, write_output_file, read_output_file, check_output
import os
import Task
import numpy as np

def solve(tasks):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    Returns:
    -   output: list of igloos in order of polishing  
    """
    tasks = SortTasks(tasks, 0) # sort tasks by deadline
    CurrSeq = []
    CurrTime = 0
    while CurrTime < 1440 and len(tasks) > 0:
        task = tasks.pop(0)
        while len(tasks) >= 1 and CurrTime + task.get_duration() > 1440: # if the current igloo cannot be completed before 1440, remove it
            task = tasks.pop(0)
        if CurrTime + task.get_duration() > 1440: # iterate until an igloo is found to not exceed the hard deadline of 1440
            break        
        CurrSeq.append(task.get_task_id())
        CurrTime += task.get_duration()
        tasks = SortTasks(tasks, CurrTime)
    return CurrSeq


def SortTasks(tasks, CurrTime):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    Output:
    -   tasks: list[Task], list of igloos to polish sorted by deadline & benefit
    """
    tasks.sort(key = lambda task: task.get_deadline()+task.get_late_benefit(CurrTime+task.get_duration()-task.get_deadline())/task.get_duration())
    return tasks





# Here's an example of how to run your solver.
# if __name__ == '__main__':
#     for input_path in os.listdir('inputs/'):
#         output_path = 'outputs/' + input_path[:-3] + '.out'
#         tasks = read_input_file(input_path)
#         output = solve(tasks)
#         write_output_file(output_path, output)

# test:
import logging
from datetime import datetime

if __name__ == '__main__':
    logging.basicConfig(filename="GreedySolver5py.log", level=logging.INFO)
    logging.info('New Log at ' + datetime.now().strftime('%m/%d/%Y %H:%M:%S'))
    benefits = {'large':[], 'medium':[], 'small':[]}
    if not os.path.isdir('outputs5/'):
        os.mkdir('outputs5/')
    for x in benefits.keys():
        if not os.path.isdir('outputs5/'+x+'/'):
            os.mkdir('outputs5/'+x+'/')
        for input_path in os.listdir('inputs/'+x+'/'):
            if input_path[-2:] == 'in':
                path = 'inputs/'+x+'/'+input_path
                tasks = read_input_file(path)
                output = solve(tasks)
                write_output_file('outputs5/'+x+'/'+input_path[:-2]+'out', output)
                tasks = read_input_file(path)
                benefits[x].append(check_output(tasks, output))
                logging.info("Profit for " + input_path + ": " + str(check_output(tasks, output)))
    for x in benefits.keys():
        benefits[x] = np.mean(benefits[x])
        print(x, benefits[x])
        logging.info(x + ': ' + str(benefits[x]))


