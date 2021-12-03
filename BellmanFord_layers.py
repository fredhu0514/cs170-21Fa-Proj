from parse import read_input_file, write_output_file
from collections import defaultdict
import time
import os

def solve(tasks):
    """
    Args:
        tasks: list[Task], list of igloos to polish
    Returns:
        output: list of igloos in order of polishing  
    """
    G = TaskGraph(tasks)
    t = 0
    while t < G.max_time:
        print(t)
        G.initialize_vertices(t)
        G.initalize_edges(t)
        BellmanFord(G)
        G.into_next_stage()
        t += 60
    best = 0 - G.global_min
    opt_path = []
    for task_index in G.global_best_path:
        #因为在G里用的是index不是真正的id，所以这里要换一下
        if task_index == -1:
            continue
        else:
            opt_path.append(G.tasks[task_index].get_task_id())
    return best, opt_path
    

class TaskGraph():
    def __init__(self, tasks, max_time = 1440):
        self.tasks = tasks
        self.task_num = len(tasks)
        self.max_time = max_time
        self.start = (-1, 0)
        self.vertices_prev = set()
        #这个用来存前一个时间段已经update完成的vertices
        self.vertices = set()
        #这个用来存需要update的vertices，下面同理
        self.edges_to_be_updated = set()
        self.edges_next_iteration = set()
        self.distances_prev = defaultdict(lambda: [float("inf") for i in range(self.task_num)])
        self.distances = defaultdict(lambda: [float("inf") for i in range(self.task_num)])
        #distance[v][i]表示不完成任务i到v的最短距离
        self.paths_prev = defaultdict(lambda: [list() for i in range(self.task_num)])
        self.paths = defaultdict(lambda: [list() for i in range(self.task_num)])

        self.global_min = 0
        #用来存目前为止最好的
        self.global_best_path = list()

        self.vertices_prev.add(self.start)
        for i in range(self.task_num):
            self.edges_to_be_updated.add((self.start, (i, 0)))
        self.distances_prev[self.start] = [0 for i in range(self.task_num)]

    def initialize_vertices(self, start_time):
        for i in range(self.task_num):
            for t in range(60):
                self.vertices.add((i, t + start_time))

    def initalize_edges(self, start_time):
        for v in self.vertices:
            next_task_start_time = v[1] + self.tasks[v[0]].get_duration()
            if next_task_start_time > start_time + 60:
                for i in range(self.task_num):
                    if i != v[0]:
                        self.edges_next_iteration.add((v, (i, next_task_start_time)))
            else:
                for i in range(self.task_num):
                    if i != v[0]:
                        self.edges_to_be_updated.add((v, (i, next_task_start_time)))

    def get_length(self, edge_to):
        task_index, t = edge_to[0], edge_to[1]
        
        deadline = self.tasks[task_index].get_deadline()
        duration = self.tasks[task_index].get_duration()
        time_late = t + duration - deadline
        return 0 - self.tasks[task_index].get_late_benefit(time_late)

    def update(self, edge):
        v, u = edge
        if v in self.vertices_prev:
            for i in range(self.task_num):
                new_distance = self.distances_prev[v][i] + self.get_length(u)
                if i != u[0]:
                    if new_distance < self.distances[u][i] and u[0] not in self.paths_prev[v][i]:
                        '''
                        =======================
                        这里需要改一下，u[0] not in self.paths_prev[v][i]这里多增加了一个linear time，可以写一个set version的dictionary而不是list，那这里就是constant time
                        =======================
                        '''
                        self.distances[u][i] = new_distance
                        self.paths[u][i] = self.paths_prev[v][i].copy()
                        self.paths[u][i].append(v[0])
                if new_distance < self.global_min and u[0] not in self.paths_prev[v][i]:
                    self.global_min = new_distance
                    self.global_best_path = self.paths_prev[v][i].copy()
                    self.global_best_path.append(v[0])
        else:
            for i in range(self.task_num):
                new_distance = self.distances[v][i] + self.get_length(u)
                if i != u[0]:
                    if new_distance < self.distances[u][i] and u[0] not in self.paths[v][i]:
                        self.distances[u][i] = new_distance
                        self.paths[u][i] = self.paths[v][i].copy()
                        self.paths[u][i].append(v[0])
                if new_distance < self.global_min and u[0] not in self.paths[v][i]:
                    self.global_min = new_distance
                    self.global_best_path = self.paths[v][i].copy()
                    self.global_best_path.append(v[0])

    def into_next_stage(self):
        del self.vertices_prev
        del self.edges_to_be_updated
        del self.distances_prev
        del self.paths_prev

        self.vertices_prev = self.vertices
        self.edges_to_be_updated = self.edges_next_iteration
        self.distances_prev = self.distances
        self.paths_prev = self.paths

        self.vertices = set()
        self.edges_next_iteration = set()
        self.distances = defaultdict(lambda: [float("inf") for i in range(self.task_num)])
        self.paths = defaultdict(lambda: [list() for i in range(self.task_num)])


def BellmanFord(G):
    for i in range(G.task_num):
        print(i)
        for e in G.edges_to_be_updated:
            G.update(e)

    

# Here's an example of how to run your solver.
# if __name__ == '__main__':
#     for input_path in os.listdir('inputs/'):
#         output_path = 'outputs/' + input_path[:-3] + '.out'
#         tasks = read_input_file(input_path)
#         output = solve(tasks)
#         write_output_file(output_path, output

if __name__ == '__main__':
    tasks = read_input_file('./samples/100.in')
    value, output = solve(tasks)
    print(value)
    print(output)
    write_output_file('./samples/100.out', output)