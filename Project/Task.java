package Project;
import java.lang.Math;


public class Task {

    private int task_id;
    private int deadline;
    private int duration;
    private double perfect_benefit;

    public Task(int task_id, int deadline, int duration, double perfect_benefit) {
        this.task_id = task_id;
        this.deadline = deadline;
        this.duration = duration;
        this.perfect_benefit = perfect_benefit;
    }

    public int get_task_id() {
        return this.task_id;
    }

    public int get_deadline() {
        return this.deadline;
    }

    public int get_duration() {
        return this.duration;
    }

    public double get_max_benefit() {
        return this.perfect_benefit;
    }

    public double get_late_benefit(int minutes_late) {
        minutes_late = Math.max(0, minutes_late);
        return this.perfect_benefit*Math.exp(-0.0170*minutes_late);
    }
}