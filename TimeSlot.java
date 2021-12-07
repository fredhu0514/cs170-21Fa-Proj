import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class TimeSlot {
    public ArrayList<Task> tasks;
    public boolean free;
    public int from;
    public int to;
    public double profit;
    public TimeSlot(int from, int to, ArrayList<Task> tasks, boolean free) {
        this.from = from;
        this.to = to;
        this.tasks = tasks;
        this.free = free;
        this.profit = TimeSlot.calculate_profit(from, tasks);
    }

    public double getProfit() {
        return TimeSlot.calculate_profit(from, tasks);
    }

    public void occupy_all_time(int time, Task task) throws Exception {
        assert (time == from && task.get_duration() == from - to && free);
    }

    public void extend_in_front(int time, Task task) throws Exception {
        assert (!free && time + task.get_duration() == from);
        this.profit += task.get_benefit_starting_time(time);
        this.from -= task.get_duration();
        this.tasks.add(0, task);
    }

    public void extend_in_back(int time, Task task) throws Exception {
        assert (!free && time == to);
        this.profit += task.get_benefit_starting_time(time);
        this.to += task.get_duration();
        this.tasks.add(task);
    }

    public ArrayList<TimeSlot> arrange_task_in_middle(int time, Task task) throws Exception {
        assert (free && time >= from && from + task.get_duration() <= to);
        return new ArrayList(List.of(new TimeSlot(from, time, new ArrayList<>(), true), new TimeSlot(time, time + task.get_duration(), new ArrayList<Task>(List.of(task)), false), new TimeSlot(time + task.get_duration(), to, new ArrayList<>(), true)));
    }

    public boolean arrangable(int time, Task task) {
        return free && time >= from && from + task.get_duration() <= to;
    }

    public static TimeSlot combine_mission_slots(TimeSlot A, TimeSlot B) {
        assert (A.to == B.from && !A.free && !B.free);
        ArrayList<Task> new_tasks = new ArrayList<>();
        new_tasks.addAll(A.tasks);
        new_tasks.addAll(B.tasks);
        return new TimeSlot(A.from, B.to, new_tasks, false);
    }

    public void push_back(int time) {
        assert (!this.free);
        this.from += time;
        this.to += time;
        this.profit = TimeSlot.calculate_profit(this.from, this.tasks);
    }

    public void go_forward(int time) {
        assert (!this.free);
        this.from -= time;
        this.to -= time;
        this.profit = TimeSlot.calculate_profit(this.from, this.tasks);
    }

    public static double calculate_profit(int time, ArrayList<Task> tasks) {
        int t = time;
        double p = 0;
        for (Task task: tasks) {
            p += task.get_benefit_starting_time(t);
            t += task.get_duration();
        }
        return p;
    }

    public double calculate_profit_loss_of_push_back(int time) {
        assert (!free);
        return this.profit - TimeSlot.calculate_profit(this.from + time, this.tasks);
    }
}
