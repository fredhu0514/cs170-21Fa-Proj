import math

class Task:
    def __init__(self, task_id: int, deadline: int, duration: int, perfect_benefit: float) -> None:
        self.task_id = task_id
        self.deadline = deadline
        self.duration = duration
        self.perfect_benefit = perfect_benefit

    def get_profit(self, curTime: int) -> float:
        return self.perfect_benefit * math.exp(-0.0170 * max(0, curTime + self.duration - self.deadline))
