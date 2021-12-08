from parse import read_input_file, write_output_file, read_output_file, check_output
import os
import Task
import numpy as np

def solve(tasks, n: int):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    -   n: the number of tasks to be selected for resorting in `SeqHelper`
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
            tasks = SortTasks(tasks, CurrTime) # Resort the task to ensure greediness
            CurrSeq, CurrTime = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks, n) # get the extra sequence of igloos that can be completed before starting the current "task"
        CurrSeq.append(task.get_task_id())
        CurrTime += task.get_duration()
    return CurrSeq


def Linear_Penalty_Profit(task, CurrTime):
    return task.get_max_benefit() - (CurrTime + task.get_duration() - task.get_deadline())

def SortTasks(tasks, CurrTime: int, By: str = 'Profit'):
    """
    Args:
    -   tasks: list[Task], list of igloos to polish
    -   CurrTime: the time already used to polish some igloos by far
    Output:
    -   tasks: list[Task], list of igloos to polish sorted by all-or-nothing benefit/duration
    """
    if By == 'Profit':
        tasks.sort(key = lambda task: Linear_Penalty_Profit(task, CurrTime)/task.get_duration(), reverse = True)
    elif By == 'Deadline':
        tasks.sort(key = lambda task: task.get_deadline())
    return tasks

def SeqHelper(CurrTime: int, TotalTime: int, CurrSeq, tasks, n: int):
    """
    This function returns an updated sequence that add additional tasks that can be completed between
    CurrTime and TotalTime
    Args:
    -   CurrTime: the time already used to polish some igloos by far
    -   CurrBenefit: the benefit attained thus far
    -   TotalTime: the time required to finish polishing all igloos
    -   CurrSeq (List[int]): the sequence of already completed task ids
    -   tasks (List[Task]): remaining tasks that might be completed sorted according to benefit/duration
    -   n: the top n tasks with highest benefit/duration is selected and resorted in increaseing deadline, 
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
        top_viable_tasks = SortTasks(top_viable_tasks[:min(n, len(top_viable_tasks))], CurrTime, By='Deadline') 
    task = top_viable_tasks[0] # choose the task with earliest deadline among top n tasks with highest benefit/duration
    tasks.remove(task)     
    TotalTimeBeforeThisTask = TotalTime - (CurrTime + task.get_duration())
    CurrSeq, CurrTime = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks, n) 
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
    logging.basicConfig(filename="GreedySolver7py.log", level=logging.INFO)
    logging.info('New Log at ' + datetime.now().strftime('%m/%d/%Y %H:%M:%S'))
    benefits = {'large':[], 'medium':[], 'small':[]}
    if not os.path.isdir('outputs7/'):
        os.mkdir('outputs7/')
    for x in benefits.keys():
        if not os.path.isdir('outputs7/'+x+'/'):
            os.mkdir('outputs7/'+x+'/')
        for input_path in os.listdir('inputs/'+x+'/'):
            if input_path[-2:] == 'in':
                path = 'inputs/'+x+'/'+input_path
                tasks = read_input_file(path)
                bestBenefit = 0
                bestOutput = []
                for n in range(1, 21):
                    output = solve(tasks, n)
                    tasks = read_input_file(path)
                    benefit = check_output(tasks, output) 
                    if benefit > bestBenefit:
                        bestBenefit = benefit
                        bestOutput = output
                logging.info('Writing output file for ' + input_path + '...' + 'Benefit = ' + str(bestBenefit))
                write_output_file('outputs7/'+x+'/'+input_path[:-2]+'out', bestOutput)
                benefits[x].append(bestBenefit)
    for x in benefits.keys():
        benefits[x] = np.mean(benefits[x])
        logging.info(x + ': ' + str(benefits[x]))


