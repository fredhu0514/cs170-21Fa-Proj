from parse import read_input_file, write_output_file
from PQ import *
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
    start = time.time()
    G.initialize_vertices()
    here1 = time.time()
    print("finished vertice initialization")
    print(here1 - start)
    '''
    G.initalize_edges()
    here2 = time.time()
    print("finished edges initialization")
    '''
    #print(here2 - here1)
    G.initialize_distances()
    G.initialize_paths()
    here2 = time.time()
    print("finished other initialization")
    print(here2 - here1)
    return BellmanFord(G)
    

class TaskGraph():
    def __init__(self, tasks, max_time = 1440):
        self.tasks = tasks
        self.task_num = len(tasks)
        self.max_time = max_time
        self.start = (-1, 0 , None)
        self.vertex = set()
        self.edges = {}
        self.distances = {}
        self.paths = {}
        self.global_min = float("inf")
        self.global_best_path = list()

    def initialize_vertices(self):
        for i in range(self.task_num):
            for t in range(self.max_time - self.tasks[i].get_duration()):
                for j in range(self.task_num):
                    if j == i:
                        self.vertex.add((i, t, -2))
                        #self.edges[(i, t, -2)] = set()
                    else:
                        self.vertex.add((i, t, j))
                        #self.edges[(i, t, j)] = set()
        self.vertex.add(self.start)
        #self.edges[self.start] = set()

    def initalize_edges(self):
        for v in self.vertex:
            self.edges[v] = self.get_tos(v)

    def get_tos(self, v):
        tos = []
        if v == self.start:
            for i in range(self.task_num):
                for j in range(self.task_num):
                    if j == i:
                        tos.append((i, 0, -2))
                    else:
                        tos.append((i, 0, j))
        else:
            t_after = v[1] + self.tasks[v[0]].get_duration()
            for i in range(self.task_num):
                if i != v[2]:
                    if (i, t_after, v[2]) in self.vertex:
                        tos.append((i, t_after, v[2]))
                else:
                    if (i, t_after, v[2]) in self.vertex:
                        tos.append((i, t_after, -2))
        return tos

    def initialize_distances(self):
        for v in self.vertex:
            self.distances[v] = float("inf")
        self.distances[self.start] = 0

    def initialize_paths(self):
        for v in self.vertex:
            self.paths[v] = list()

    def get_length(self, to):
        task_index, t = to[0], to[1]
        
        deadline = self.tasks[task_index].get_deadline()
        duration = self.tasks[task_index].get_duration()
        time_late = t + duration - deadline
        return - self.tasks[task_index].get_late_benefit(time_late)

    def update(self, v, u):
        new_distance = self.distances[v] + self.get_length(u)
        if new_distance < self.distances[u] and u[0] not in self.paths[v]:
            new_path = self.paths[v].copy()
            new_path.append(u[0])
            self.distances[u] = new_distance
            self.paths[u] = new_path
            if new_distance < self.global_min:
                self.global_min = new_distance
                self.global_best_path = new_path.copy()

def BellmanFord(G):
    start = time.time()
    print(len(G.vertex))
    print("total num vertex")
    for k in range(len(G.vertex)):
        print(k)
        here = time.time()
        print(here - start)
        start = here
        for v in G.vertex:
            for u in G.get_tos(v):
                G.update(v, u)
        print("global min:      " + str(global_min))

    min_dis = float("inf")
    opt_path = None
    for v in G.vertex:
        if G.distances[v] < min_dis:
            min_dis = G.distances[v]
            opt_path = G.paths[v]

    opt_ids = list()
    for index in opt_path:
        opt_ids.append(G.tasks[index].get_task_id())
    return opt_ids
    

# Here's an example of how to run your solver.
# if __name__ == '__main__':
#     for input_path in os.listdir('inputs/'):
#         output_path = 'outputs/' + input_path[:-3] + '.out'
#         tasks = read_input_file(input_path)
#         output = solve(tasks)
#         write_output_file(output_path, output

if __name__ == '__main__':
    tasks = read_input_file('./samples/100.in')
    output = solve(tasks)
    print(output)
    write_output_file('./samples/100.out', output)