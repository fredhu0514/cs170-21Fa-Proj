from parse import read_input_file, write_output_file
from PQ import *
import os

def solve(tasks):
    """
    Args:
        tasks: list[Task], list of igloos to polish
    Returns:
        output: list of igloos in order of polishing  
    """
    G = TaskGraph(tasks)
    G.initialize_vertices()
    G.initialize_edges()
    G.initialize_distances()
    G.initialize_paths()
    return Dijkstra(G)
    

class TaskGraph():
    def __init__(self, tasks, max_time = 1440):
        self.tasks = tasks
        self.task_num = len(tasks)
        self.max_time = max_time
        self.start = ("start", 0 , None)
        self.vertex = set()
        self.edges = {}
        self.distances = {}
        self.paths = {}

    def initialize_vertices(self):
        for i in range(self.task_num):
            for t in range(self.max_time - self.tasks[v[0]].get_duration()):
                for j in range(self.task_num):
                    if j == i:
                        continue
                    else:
                        self.vertex.add((i, t, j))
                        self.edges[(i, t, j)] = set()
                self.vertex.add((i, t, "Done"))
        self.vertex.add(self.start)
        self.edges[self.start] = set()

    def initialize_edges(self):
        for v in self.vertex:
            if v == self.start:
                continue
            t_after = v[1] + self.tasks[v[0]].get_duration()
            for i in range(self.task_num):
                if i != v[0]:
                    if (i, t_after, v[2]) in self.vertex:
                        self.edges[v].add((i, t_after, v[2]))
                else:
                    self.edges[v].add((i, t_after, "Done"))
            if v[1] == 0:
                self.edges[self.start].add(v)


    def initialize_distances(self):
        for v in self.vertex:
            self.distances[v] = float("inf")
        self.distances[self.start] = 0

    def initialize_paths(self):
        for v in self.vertex:
            self.paths[v] = list()

    def get_length(to):
        task_index, t = to[0], to[1]
        
        deadline = self.tasks[task_index].get_deadline()
        duration = self.tasks[task_index].get_duration()
        time_late = t + duration - deadline
        return 100 - self.tasks[task_index].get_late_benefit(time_late)

def Dijkstra(G):
    fringe = UpdateableQueue()
    for v in G.vertex:
        fringe.push(v, float("inf"))
    fringe.push(G.start, 0)
    
    while len(fringe) > 0:
        processing = fringe.pop()
        for to in G.edges[processing]:
            new_distance = G.distances[processing] + G.get_length(to)
            if new_distance < G.distances[to] and to[0] not in G.paths[processing]:
                new_path = G.paths[processing].copy()
                new_path.append(to[0])
                G.distances[to] = new_distance
                G.paths[to] = new_path
                fringe.push(to, new_distance)

    min_dis = float("inf")
    opt_path = None
    for v in G.vertex:
        if G.distances[v] < min_dis:
            min_dis = G.distances[v]
            opt_path = G.paths[v]

    return opt_path

# Here's an example of how to run your solver.
# if __name__ == '__main__':
#     for input_path in os.listdir('inputs/'):
#         output_path = 'outputs/' + input_path[:-3] + '.out'
#         tasks = read_input_file(input_path)
#         output = solve(tasks)
#         write_output_file(output_path, output

if __name__ == '__main__':
    tasks = read_input_file('./samples/simple.in')
    output = solve(tasks)
    print(output)
    write_output_file('./samples/simple.out', output)