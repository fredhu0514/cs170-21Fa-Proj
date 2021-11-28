from parse import read_input_file, write_output_file, read_output_file, check_output
import os
import Task
import numpy as np

def solve_iter(tasks, max_n):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    -   max_n: the max number of tasks to be selected for resorting in `SeqHelper`
    Returns:
    -   output: list of igloos in order of polishing  
    """
    tasks_copy = tasks.copy()
    OptSeq = []
    OptBenefit = 0
    for i in range(max_n*1000):
        output = solve(tasks, max_n) 
        benefit = check_output(tasks_copy, output)
        if benefit > OptBenefit:
            OptBenefit = benefit
            OptSeq = output
        tasks = tasks_copy
        tasks_copy = tasks.copy()
    return (OptSeq, OptBenefit)
    

def solve(tasks, max_n):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    -   max_n: the max number of tasks to be selected for resorting in `SeqHelper`
    Returns:
    -   output: list of igloos in order of polishing  
    """
    tasks = SortTasks(tasks, 0, 1440) # sort tasks by max_benefit/duration
    CurrSeq = []
    CurrTime = 0
    while CurrTime < 1440 and len(tasks) > 0:
        top_viable_tasks = [task for task in tasks if CurrTime + task.get_duration() <= 1440] # find tasks that can be completed before 1440
        if len(top_viable_tasks) == 0:
            break
        else:
            task = top_viable_tasks[0]
        tasks.remove(task)     
        TotalTimeBeforeThisTask = task.get_deadline() - (CurrTime + task.get_duration())
        if TotalTimeBeforeThisTask > 0:
            CurrSeq, CurrTime = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks, max_n) # get the extra sequence of igloos that can be completed before starting the current "task"
        CurrSeq.append(task.get_task_id())
        CurrTime += task.get_duration()
    return CurrSeq


def SortTasks(tasks, CurrTime: int, TotalTime: int, By: str = 'Profit'):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    -   CurrTime: the time already used to polish some igloos by far
    -   TotalTime: the time required to finish polishing all igloos
    -   By: 'Profit' or 'Deadline'
    Output:
    -   tasks: list[Task], list of igloos to polish sorted by benefit/duration in descending order or by deadline in ascending order
    """
    if By == 'Profit':
        tasks.sort(key = lambda task: task.get_late_benefit(CurrTime + task.get_duration() - TotalTime)/task.get_duration(), reverse = True)
    elif By == 'Deadline':
        tasks.sort(key = lambda task: task.get_deadline())
    return tasks

def SeqHelper(CurrTime: int, TotalTime: int, CurrSeq, tasks, max_n: int):
    """
    This function returns an updated sequence that add additional tasks that can be completed between
    CurrTime and TotalTime
    Args:
    -   CurrTime: the time already used to polish some igloos by far
    -   CurrBenefit: the benefit attained thus far
    -   TotalTime: the time required to finish polishing all igloos
    -   CurrSeq (List[int]): the sequence of already completed task ids
    -   tasks (List[Task]): remaining tasks that might be completed sorted according to benefit/duration
    -   max_n: the top n tasks (with n between 1 and max_n inclusive) with highest benefit/duration is selected and resorted in increaseing deadline, 
        and the task with the earliest deadline is chosen
    Output:
    -   CurrSeq: the updated sequence of already completed tasks
    -   CurrTime: the updated time already used to polish igloos by far
    """
    top_viable_tasks = [task for task in tasks if CurrTime + task.get_duration() <= TotalTime and task.get_deadline() >= TotalTime] 
    # find tasks that can be completed before TotalTime and with deadline after this time
    if len(top_viable_tasks) == 0:
        return (CurrSeq, CurrTime)
    else:
        n = int(np.random.randint(low=1,high=max_n, size=1))
        top_viable_tasks = SortTasks(top_viable_tasks[:min(n, len(top_viable_tasks))], CurrTime, TotalTime, By='Deadline') 
    task = top_viable_tasks[0] # choose the task with earliest deadline among top n tasks with highest benefit/duration
    tasks.remove(task)     
    TotalTimeBeforeThisTask = TotalTime - (CurrTime + task.get_duration())
    CurrSeq, CurrTime = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks, max_n) 
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
    logging.basicConfig(filename="GreedySolver4.log", level=logging.INFO)
    logging.info('New Log at ' + datetime.now().strftime('%m/%d/%Y %H:%M:%S'))
    if not os.path.isdir('outputsNew/'):
            os.mkdir('outputsNew/')
    benefits = {'small':[], 'medium':[], 'large':[]}
    for x in benefits.keys():
        if not os.path.isdir('outputsNew/'+x+'/'):
            os.mkdir('outputsNew/'+x+'/')
    for x in benefits.keys():
        logging.info(x + ': ')
        for input_path in os.listdir('inputs/'+x+'/'):
            if input_path[-2:] == 'in':
                path = 'inputs/'+x+'/'+input_path
                tasks = read_input_file(path)
                output, benefit = solve_iter(tasks, len(tasks)//5)
                logging.info('Writing output file for ' + input_path + '...')
                write_output_file('outputsNew/'+x+'/'+input_path[:-2]+'out', output)
                benefits[x].append(benefit)
        for x in benefits.keys():
            benefits[x] = np.mean(benefits[x])
            logging.info(x + ': ' + str(benefits[x]))



