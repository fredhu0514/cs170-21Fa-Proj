from parse import read_input_file, write_output_file
import os


def solve(tasks):
    """
    Args:
        tasks: list[Task], list of igloos to polish
    Returns:
        output: list of igloos in order of polishing  
    """
    tasks.sort(key=lambda task: 0 - task.get_max_benefit() / task.get_duration())
    total_time_range = [(0, 1440, True, [])]
    time_left = 1440

    for task in tasks:
        #import pdb; pdb.set_trace()
        #print(total_time_range)
        #print(time_left)
        if task.get_duration() > time_left:
            continue
        before_time_available = 0
        best_start_time = task.get_deadline() - task.get_duration()
        for i in range(len(total_time_range)):
            time_slot = total_time_range[i]
            if time_slot[1] < best_start_time:
                if time_slot[2]:
                    before_time_available += time_slot[1] - time_slot[0]
            else:
                if time_slot[2]:
                    if time_slot[1] >= task.get_deadline() and time_slot[1] - time_slot[0] >= task.get_duration():
                        total_time_range = arrange_task(total_time_range, i, task, best_start_time)
                        time_left -= task.get_duration()
                        break
                    elif time_slot[1] - time_slot[0] >= task.get_duration():
                        total_time_range = arrange_task(total_time_range, i, task, time_slot[1] - task.get_duration())
                        time_left -= task.get_duration()
                        break
                    else:
                        if before_time_available + time_slot[1] - time_slot[0] >= task.get_duration():
                            total_time_range = rearrange_task(total_time_range, i, task)
                            time_left -= task.get_duration()
                            break
                        else:
                            #print(total_time_range)
                            total_time_range = all_forward_push(total_time_range, i)
                            total_time_range = rearrange_task_backwards(total_time_range, 0, task)
                            #print(total_time_range)
                            #import pdb; pdb.set_trace()
                            time_left -= task.get_duration()
                            break
                else:
                    if before_time_available >= task.get_duration():
                        total_time_range = rearrange_task(total_time_range, i, task)
                        time_left -= task.get_duration()
                        break
                    else:
                        #print(total_time_range)
                        
                        total_time_range = all_forward_push(total_time_range, i)
                        total_time_range = rearrange_task_backwards(total_time_range, 0, task)
                        time_left -= task.get_duration()
                        #print(total_time_range)
                        #import pdb; pdb.set_trace()
                        break
        

    tasks = []
    print(total_time_range)
    for time_slot in total_time_range:
        tasks += time_slot[3]

    output = []
    profit = 0
    cur_time = 0
    #print(tasks)
    for task in tasks:
        output.append(task.get_task_id())
        profit += task.get_benefit_starting_time(cur_time)
        cur_time += task.get_duration()

    print(output)
    print(profit)
    print(cur_time)
    return output


def all_forward_push(total_time_range, index):
    #import pdb; pdb.set_trace()
    rearranged_tasks = []
    task_time = 0
    free_time = 0
    for i in range(index + 1):
        rearranged_tasks += total_time_range[i][3]
        if not total_time_range[i][2]:
            task_time += total_time_range[i][1] - total_time_range[i][0]
        else:
            free_time += total_time_range[i][1] - total_time_range[i][0]
    right = total_time_range[index + 1:]
    return [[0, task_time, False, rearranged_tasks]] + [[task_time, task_time + free_time, True, []]] + right


def rearrange_task_backwards(total_time_range, index, task):
    #import pdb; pdb.set_trace()
    rearranged_tasks = [task]
    time_got = 0
    i = index
    time_needed = task.get_duration()
    while True:
        #print(i)
        #print(time_needed)
        if total_time_range[i][2]:
            time_have = total_time_range[i][1] - total_time_range[i][0]
            if (time_have > time_needed):
                break;
            else:
                time_needed -= time_have
        else:
            rearranged_tasks = rearranged_tasks + total_time_range[i][3]
        i += 1
    left = total_time_range[:index].copy()
    right = total_time_range[i + 1:].copy()
    rearranged_tasks = [[total_time_range[index][0], total_time_range[i][1] - time_have + time_needed, False, rearranged_tasks]]
    cut = [[total_time_range[i][1] - time_have + time_needed, total_time_range[i][1], True, []]]
    return left + rearranged_tasks + cut + right


def rearrange_task(total_time_range, index, task):
    #import pdb; pdb.set_trace()
    rearranged_tasks = [task]
    time_got = 0
    i = index
    time_needed = task.get_duration()
    while True:
        if total_time_range[i][2]:
            time_have = total_time_range[index][1] - total_time_range[index][0]
            if (time_have > time_needed):
                break;
            else:
                time_needed -= time_have
        else:
            rearranged_tasks = total_time_range[i][3] + rearranged_tasks
        i -= 1
    left = total_time_range[:i].copy()
    right = total_time_range[index + 1:].copy()
    cut = [[total_time_range[i][0], total_time_range[i][0] + time_have - time_needed, True, []]]
    rearranged_tasks = [
        [total_time_range[i][0] + time_have - time_needed, total_time_range[index][1], False, rearranged_tasks]]
    return left + cut + rearranged_tasks + right


def arrange_task(total_time_range, index, task, time):
    left = total_time_range[:index].copy()
    right = total_time_range[index + 1:].copy()
    new = [[total_time_range[index][0], time, True, []], [time, time + task.get_duration(), False, [task]],
           [time + task.get_duration(), total_time_range[index][1], True, []]]
    return left + new + right


if __name__ == '__main__':
    tasks = read_input_file('./samples/100.in')
    output = solve(tasks)
    print(output)
    write_output_file('./100.out', output)
