import math

DECAY_RATE = 0.000825

class Task:
    def __init__(self, task_id: int, deadline: int, duration: int, perfect_benefit: float) -> None:
        self.task_id = task_id
        self.deadline = deadline
        self.duration = duration
        self.perfect_benefit = perfect_benefit

    def get_profit(self, curTime: int) -> float:
        return self.perfect_benefit * math.exp(-0.0170 * max(0, curTime + self.duration - self.deadline))

    def get_decay_profit(self, curTime):
        if curTime + self.duration > 1440:
            return -1.0
        benefit_per_timestamp = self.perfect_benefit * math.exp(
            -0.0170 * max(0, curTime + self.duration - self.deadline)) / self.duration
        decay_profit = 0
        for i in range(self.duration):
            decay_profit += math.exp(-DECAY_RATE * i) * benefit_per_timestamp
        return decay_profit / self.duration