# CS 170 Project Fall 2021

## The algorithm in `solver.py` does the following iteratively:
1) Find the task with highest max_profit/duration. (`Sorttask`)
2) If there are tasks that can be completed before this task provided that this task is guaranteed to be completed before its deadline, find additional tasks with highest max_profit/duration that can be completed and with deadline before this task. (`SeqHelper`)
3) Add the tasks to optimal sequence.
4) Stop when the total time to complete all tasks is just about to exceed 1440 or if we run out of tasks.

## Time Complexity & Space Complexity:
Let `m` = `len(tasks)`:
Time = `O(m)` (to sort task once) + `O(m)` (# of iterations) * `O(m)` (# of tasks that have to be iterated through in one `SeqHelper` recursion) * `O(m)` (# of `SeqHelper` recursion to be called per iteration, using the fact that at least one task can be removed during each recursion) = `O(m^3)`

Space = `O(m)` (sorted tasks, optimal sequence, current time)

## The algorithm in `solver2.py` does the following iteratively:
1) Find the task with highest max_profit/duration. (`Sorttask`)
2) If there are tasks that can be completed before this task provided that this task is guaranteed to be completed before its deadline, complete one such task that has the earliest deadline among all top `n` such tasks with highest max_profit/duration. (`n` is a hyperparameter) Recurse on this process until no more tasks can be completed within this interval.  (`SeqHelper`)
3) Add the tasks to optimal sequence.
4) Stop when the total time to complete all tasks is just about to exceed 1440 or if we run out of tasks.

## Time Complexity & Space Complexity:
Time = `O(m)` (to sort task once) + `O(m)` (# of iterations) * `O(m)` (# of tasks that have to be iterated through in one `SeqHelper` recursion) * `O(m)` (# of `SeqHelper` recursion to be called per iteration, using the fact that at least one task can be removed during each recursion) = `O(m^3)` (Since `n` can be considered as a constant, it takes an extra constant time in each recursion to sort the top `n` viable tasks)

Space = `O(m)` (sorted tasks, optimal sequence, current time)

## 2 Log Files Store The Results

