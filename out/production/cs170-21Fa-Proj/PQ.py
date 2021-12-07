import heapq

class UpdateableQueue:
    def __init__(self,iterable=None):
        self.heap = []
        self.priority_values = {}
        if iterable:
            for item in iterable:
                self.priority_values[item[0]] = item[1]
                heapq.heappush(self.heap,(item[1],item[0]))

    def __getitem__(self, key):
        if key in self.priority_values:
            return self.priority_values[key]
        else:
            raise KeyError('Item not found in the priority queue')

    def __len__(self):
        return len(self.priority_values)

    def __contains__(self, key):
        return key in self.priority_values

    def push(self, key, priority):
        self.priority_values[key] = priority
        #print(type(key))
        #print(type(priority)
        
        heapq.heappush(self.heap, (float(priority), key))

    def pop(self):
        if not self.heap:
            raise IndexError("The heap is empty")

        value,key = self.heap[0]
        while key not in self or self.priority_values[key] != value:
            heapq.heappop(self.heap)
            if not self.heap:
                raise IndexError("The heap is empty")
            value,key = self.heap[0]

        value, key = heapq.heappop(self.heap)
        del self.priority_values[key]
        return key