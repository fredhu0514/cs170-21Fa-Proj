from parse import read_input_file, write_output_file
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
    return Bellman_Ford(G, len(tasks))
    

class TaskGraph():
    def __init__(self, tasks, max_time = 1440):
        self.tasks = tasks
        self.task_num = len(tasks)
        self.max_time = max_time
        self.start = ("start", 0)
        self.vertex = set()
        self.edges = set()
        self.distances = {}
        self.mins = {}
        self.paths = {}
        self.min_paths = {}

    def initialize_vertices(self):
        for i in range(self.task_num):
            for t in range(self.max_time):
                duration = self.tasks[i].get_duration()
                if t + duration > self.max_time:
                    break
                else:
                    self.vertex.add((i, t))
        self.vertex.add(self.start)

    def initialize_edges(self):
        for v in self.vertex:
            if v == self.start:
                continue
            t_after = v[1] + self.tasks[v[0]].get_duration()
            for i in range(self.task_num):
                if i != v[0] and (i, t_after) in self.vertex:
                    self.edges.add((v, (i, t_after)))
            if v[1] == 0:
                self.edges.add((self.start, v))


    def initialize_distances(self):
        for v in self.vertex:
            self.distances[v] = [float("inf") for i in range(self.task_num)]
            #the ith value in self.distances[v] is the shortest path to reach v without finishing ith task
            self.mins[v] = float("inf")
            #the overall min path to reach v
        self.distances[self.start] = [0 for i in range(self.task_num)]
        self.mins[self.start] = 0

    def initialize_paths(self):
        for v in self.vertex:
            self.paths[v] = [list() for i in range(self.task_num)]
            self.min_paths[v] = list()

    def update(self, edge):
        v1 = edge[0]
        v2 = edge[1]
        
        task_index1, t1 = v1[0], v1[1]
        task_index2, t2 = v2[0], v2[1]

        length = self.get_length(edge)

        for i in range(self.task_num):
            new_path_length = self.distances[v1][i] + length
            if i != task_index2:
                if new_path_length < self.distances[v2][i] and task_index2 not in self.paths[v1][i]:
                    new_path = self.paths[v1][i].copy()
                    new_path.append(v2[0])
                    self.distances[v2][i] = new_path_length
                    self.paths[v2][i] = new_path
                if new_path_length < self.mins[v2] and task_index2 not in self.paths[v1][i]:
                    new_path = self.paths[v1][i].copy()
                    new_path.append(v2[0])
                    self.mins[v2] = new_path_length
                    self.min_paths[v2] = new_path
            else:
                if new_path_length < self.mins[v2] and task_index2 not in self.paths[v1][i]:
                    new_path = self.paths[v1][i].copy()
                    new_path.append(v2[0])
                    self.mins[v2] = new_path_length
                    self.min_paths[v2] = new_path

    def get_length(self, edge):
        v1 = edge[0]
        v2 = edge[1]

        task_index2, t2 = v2[0], v2[1]
        
        deadline2 = self.tasks[task_index2].get_deadline()
        duration2 = self.tasks[task_index2].get_duration()
        time_late = t2 + duration2 - deadline2
        return 0 - self.tasks[task_index2].get_late_benefit(time_late)

def Bellman_Ford(G, num_iteration):
    for _ in range(num_iteration):
        for e in G.edges:
            G.update(e)

    min_reward = float("inf")
    path = None

    #print(G.mins)

    for v in G.vertex:
        if G.mins[v] < min_reward:
            #print(v)
            min_reward = G.mins[v]
            path = G.min_paths[v]

    task_ids = []
    for v in path:
        task_ids.append(G.tasks[v].get_task_id())

    return task_ids


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
    write_output_file('./100.out', output)