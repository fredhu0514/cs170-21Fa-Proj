# CS 170 Project Fall 2021

The algorithm does the following iteratively:
1) Find the task with highest max_profit/duration. (`Sorttask`)
2) If there are tasks that can be completed before this task provided that this task is guaranteed to be completed before its deadline, find additional tasks with highest max_profit/duration that can be completed and with deadline before this task. (`SeqHelper`)
3) Add the tasks to optimal sequence.
4) Stop when the total time to complete all tasks is just about to exceed 1440.
