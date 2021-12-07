import parse as p
import numpy as np
import pandas as pd
from sklearn import svm
import pickle
import matplotlib.pyplot as plt

TRAIN_DATA = []  # [curTime, duration, ddl, benefitAtCurTime, label]]


def add_data(output_list, task_list):
    curTime = 0
    for task_id in range(len(output_list)):
        curTask = task_list[output_list[task_id] - 1]
        curData = [curTime, curTask.duration, curTask.deadline, curTask.get_profit(curTime), 1]
        TRAIN_DATA.append(curData)
        if task_id + 1 < len(output_list):
            for fake_id in range(task_id + 1, len(output_list)):
                fakeTask = task_list[output_list[fake_id] - 1]
                fakeData = [curTime, fakeTask.duration, fakeTask.deadline, fakeTask.get_profit(curTime), 0]
                TRAIN_DATA.append(fakeData)
        curTime += curTask.duration


for _type in ["medium"]:
    for t_id in range(1, 300):
        task_list = p.read_input_file(f"./inputs/{_type}/{_type}-{t_id}.in")
        output_list = p.read_output_file(f"./train/{_type}/{_type}-{t_id}.out")
        add_data(output_list, task_list)

data = np.array(TRAIN_DATA)
df = pd.DataFrame(data, columns=['cur_time', 'duration', 'ddl', 'cur_profit', 'label'])
print(df.head(5))
print(len(df[df["label"] == 0]))
print(len(df[df["label"] == 1]))


pos_df = df[df["label"] == 1]
neg_df = df[df["label"] == 0]
neg_df = neg_df.sample(len(pos_df))
train_df = pos_df.append([neg_df])


clf = svm.SVC()
clf.fit(train_df[['cur_time', 'duration', 'ddl', 'cur_profit']], train_df["label"])
filename = 'medium_svm.sav'
pickle.dump(clf, open(filename, 'wb'))


pys = clf.predict(df[['cur_time', 'duration', 'ddl', 'cur_profit']])
plt.scatter(df[["cur_time"]], df[["cur_profit"]], c=["red" if x == 0 else "blue" for x in pys])
plt.show()

