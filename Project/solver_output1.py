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
    tasks = SortTasks(tasks, 0, 1440) # sort tasks by max_benefit/duration
    CurrSeq = []
    CurrTime = 0
    while CurrTime < 1440 and len(tasks) > 0:
        task = tasks.pop(0)
        while len(tasks) >= 1 and CurrTime + task.get_duration() > 1440: # if the current igloo cannot be completed before 1440, remove it
            task = tasks.pop(0)
        if CurrTime + task.get_duration() > 1440: # iterate until an igloo is found to not exceed the hard deadline of 1440
            break        
        TotalTimeBeforeThisTask = task.get_deadline() - (CurrTime + task.get_duration())
        if TotalTimeBeforeThisTask > 0:
            #tasks = SortTasks(tasks, CurrTime, CurrTime + TotalTimeBeforeThisTask) # Resort the task to ensure greediness
            CurrSeq, CurrTime = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks) # get the extra sequence of igloos that can be completed before starting the current "task"
        CurrSeq.append(task.get_task_id())
        CurrTime += task.get_duration()
        #tasks = SortTasks(tasks, CurrTime, 1440)
    return CurrSeq


def SortTasks(tasks, CurrTime: int, TotalTime: int):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    -   CurrTime: the time already used to polish some igloos by far
    -   TotalTime: the time required to finish polishing all igloos
    Output:
    -   tasks: list[Task], list of igloos to polish sorted by benefit/duration
    """
    tasks.sort(key = lambda task: task.get_late_benefit(CurrTime + task.get_duration() - TotalTime)/task.get_duration(), reverse = True)
    return tasks

def SeqHelper(CurrTime: int, TotalTime: int, CurrSeq, tasks):
    """
    This function returns an updated sequence that add additional tasks that can be completed between
    CurrTime and TotalTime
    Args:
    -   CurrTime: the time already used to polish some igloos by far
    -   CurrBenefit: the benefit attained thus far
    -   TotalTime: the time required to finish polishing all igloos
    -   CurrSeq (List[int]): the sequence of already completed task ids
    -   tasks (List[Task]): remaining tasks that might be completed sorted according to benefit/duration
    Output:
    -   CurrSeq: the updated sequence of already completed tasks
    -   CurrTime: the updated time already used to polish igloos by far
    """
    j = 0
    while j < len(tasks) and (CurrTime + tasks[j].get_duration() > TotalTime or tasks[j].get_deadline() <= TotalTime): 
        j += 1
    if j == len(tasks): # no additional tasks can be completed between this interval and have deadline before this interval
        return (CurrSeq, CurrTime)
    else:
        task = tasks.pop(j)
        TotalTimeBeforeThisTask = TotalTime - (CurrTime + task.get_duration())
        #tasks = SortTasks(tasks, CurrTime, CurrTime + TotalTimeBeforeThisTask) # Resort the task to ensure greediness
        CurrSeq, CurrTime = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks) # get the extra sequence of igloos that can be completed before starting the current "task"
        assert CurrTime <= TotalTime, 'SeqHelper Recursion Error'
        CurrSeq.append(task.get_task_id())
        CurrTime += task.get_duration()
        assert CurrTime <= TotalTime
        return (CurrSeq, CurrTime)





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
    logging.basicConfig(filename="GreedySolver1py.log", level=logging.INFO)
    logging.info('New Log at ' + datetime.now().strftime('%m/%d/%Y %H:%M:%S'))
    benefits = {'small':[], 'medium':[], 'large':[]}
    if not os.path.isdir('outputs1/'):
        os.mkdir('outputs1/')
    for x in benefits.keys():
        if not os.path.isdir('outputs1/'+x+'/'):
            os.mkdir('outputs1/'+x+'/')
        for input_path in os.listdir('inputs/'+x+'/'):
            if input_path[-2:] == 'in':
                path = 'inputs/'+x+'/'+input_path
                tasks = read_input_file(path)
                output = solve(tasks)
                write_output_file('outputs1/'+x+'/'+input_path[:-2]+'out', output)
                tasks = read_input_file(path)
                benefits[x].append(check_output(tasks, output))
                logging.info("Profit for " + input_path + ": " + str(check_output(tasks, output)))
    for x in benefits.keys():
        benefits[x] = np.mean(benefits[x])
        print(x, benefits[x])
        logging.info(x + ': ' + str(benefits[x]))


