import parse as p
import numpy as np

filename = "./train/small/small-1."
filename1 = filename + "in"
filename2 = filename + "out"
task_list = p.read_input_file(filename1)
output_list = p.read_output_file(filename2)

TRAIN_DATA = [] # [curTime, duration, ddl, benefitAtCurTime, label]]
curTime = 0
for task_id in range(len(output_list)):
    curTask = task_list[output_list[task_id]-1]
    curData = [curTime, curTask.duration, curTask.deadline,  curTask.get_profit(curTime), 1]
    TRAIN_DATA.append(curData)
    if task_id + 1 < len(output_list):
        for fake_id in range(task_id + 1, len(output_list)):
            fakeTask = task_list[output_list[fake_id]-1]
            fakeData = [curTime, fakeTask.duration, fakeTask.deadline, fakeTask.get_profit(curTime), 0]
            TRAIN_DATA.append(fakeData)
    curTime += curTask.duration

print(TRAIN_DATA)