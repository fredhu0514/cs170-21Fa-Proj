import java.lang.Math;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;


class Triple {
    private double first;
    private int second;
    public ArrayList<Integer> third;
    public Triple(double first, int second, ArrayList<Integer> third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public double getFirst() {return this.first;}

    public int getSecond() {
        return second;
    }

    public ArrayList<Integer> getThird() {
        return third;
    }
}

public class Path_Record_Solver {

    public static class Task {

        private final int task_id;
        private final int deadline;
        private final int duration;
        private final double perfect_benefit;

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

        public double get_benefit_starting_time(int time) {
            return this.get_late_benefit(time + this.duration - this.deadline);
        }

        public double get_max_benefit() {
            return this.perfect_benefit;
        }

        public double get_late_benefit(int minutes_late) {
            minutes_late = Math.max(0, minutes_late);
            return this.perfect_benefit*Math.exp(-0.0170*minutes_late);
        }
    }

    public class parse {

        public parse() {
        }

        public boolean isDigit(String s) {
            try {
                int x = Integer.parseInt(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public List<Task> read_input_file(String filename) throws Exception {
            List<Task> Tasks = new ArrayList<>();

            List<String> input_lines = Files.readAllLines(Paths.get(filename));

            assert (isDigit(input_lines.get(0).split("\t", 0)[0])) : "First line is not a valid number of tasks";
            int num_tasks = Integer.parseInt(input_lines.get(0).split("\t", 0)[0]);
            assert (num_tasks == input_lines.size() - 1) : "The number of tasks in the first line of the input file does not match the tasks defined in the rest of the input file";
            assert (num_tasks <= 200) : "Too many tasks";

            for (int i = 1; i <= num_tasks; i += 1) {
                String[] task_parameters = input_lines.get(i).split("\t", 0);
                if (task_parameters.length != 4) {
                    task_parameters = input_lines.get(i).split(" ", 0);
                }

                assert (task_parameters.length == 4) : String.format("The number of parameters in task %d is incorrect", i);

                assert (isDigit(task_parameters[0])) : String.format("Task %d has an invalid task id %s", i, task_parameters[0]);
                int task_id = Integer.parseInt(task_parameters[0]);
                assert (task_id == i) : String.format("Task %d has an invalid task id %d", i, task_id);

                assert (isDigit(task_parameters[1])) : String.format("Task %d has an invalid deadline %s", i, task_parameters[1]);
                int deadline = Integer.parseInt(task_parameters[1]);
                assert (deadline > 0 & deadline <= 1440) : String.format("Task %d has an invalid deadline %d", i, deadline);

                assert (isDigit(task_parameters[2])) : String.format("Task %d has an invalid duration %s", i, task_parameters[2]);
                int duration = Integer.parseInt(task_parameters[2]);
                assert (duration > 0 & duration <= 60) : String.format("Task %d has an invalid duration %d", i, duration);

                double max_benefit;

                try {
                    max_benefit = Double.parseDouble(task_parameters[3]);
                } catch (Exception e) {
                    System.out.println(String.format("Task %d has an invalid non-float max benefit %s", i, task_parameters[3]));
                    return null;
                }
                assert (max_benefit > 0 & max_benefit < 100.0) : String.format("Task %d has an invalid max benefit %d", i, max_benefit);

                Task task = new Task(task_id, deadline, duration, max_benefit);
                Tasks.add(task);
            }
            return Tasks;
        }

        public List<Integer> read_output_file(String filename) throws Exception {
            List<Integer> task_ids = new ArrayList<>();

            List<String> input_lines = Files.readAllLines(Paths.get(filename));
            int num_tasks = input_lines.size();
            assert (num_tasks <= 200) : "Too many tasks scheduled. Make sure you are not repeating any tasks.";

            for (int i = 0; i < num_tasks; i++) {
                assert (input_lines.get(i).split("\t", 0).length == 1) : String.format("Invalid number of items on line %d", i);
                String s = input_lines.get(i).split("\t", 0)[0];
                assert (isDigit(s)) : String.format("Invalid task id %s", s);
                int task_id = Integer.parseInt(s);
                assert (task_id >= 1 & task_id <= 200) : String.format("Invalid task id %d", task_id);
                assert (!task_ids.contains(task_id)) : String.format("task_id %d appears more than once", task_id);
                task_ids.add(task_id);
            }
            return task_ids;
        }

        public void write_output_file(String filename, List<Integer> task_ids) throws Exception {
            assert (task_ids.size() <= 200) : "Too many tasks scheduled. Make sure you are not repeating any tasks.";

            List<String> output_lines = new ArrayList<>();

            for (Integer task_id : task_ids) {
                assert (task_id >= 1 & task_id <= 200) : String.format("Invalid task id %d", task_id);
                output_lines.add(String.format("%d", task_id));
            }
            Files.write(Paths.get(filename), output_lines);
        }
    }

    public static class solver {

        private int get_index(int task_index, int time) {
            if (task_index == -1) {
                return -1;
            }
            return task_index * 60 + time;
        }

        private void initialize(int num, ArrayList<Double> profit, ArrayList<ArrayList<Integer>> path_list, ArrayList<HashSet<Integer>> path_set, Boolean head) {
            if (head) {
                for (int i = 1; i <= num; i ++) {
                    profit.add(0.0);
                    path_list.add(new ArrayList<Integer>());
                    path_set.add(new HashSet<Integer>());
                }
            } else {
                for (int i = 1; i <= num; i ++) {
                    profit.add(Double.NEGATIVE_INFINITY);
                    path_list.add(new ArrayList<Integer>());
                    path_set.add(new HashSet<Integer>());
                }
            }

        }



        public List<Integer> solve(List<Task> tasks, int Max_time, int time_interval) {
        /*
        DP solving based on time interval of 60
        Uses (i, t) as finishing task i at time t
        Note, i here means index in the list of tasks, instead of the true task id.
         */
            int task_num = tasks.size();
            double global_best = Double.NEGATIVE_INFINITY;
            ArrayList<Integer> global_best_path = new ArrayList<>();

            HashMap<HashSet<Integer>, Triple> now = new HashMap<>();
            HashMap<HashSet<Integer>, Triple> next = new HashMap<>();
            HashMap<Integer, HashSet<HashSet<Integer>>> to_be_updated = new HashMap<>();

            now.put(new HashSet<>(), new Triple(0, 0, new ArrayList()));

            for (int time = 1; time < Max_time; time += time_interval) {
                System.out.println(time);
                for (HashSet<Integer> set_task : now.keySet()) {
                    for (int i = 0; i < task_num; i ++) {
                        if (!set_task.contains(i)) {
                            int original_time = now.get(set_task).getSecond();
                            int new_time = original_time + tasks.get(i).get_duration();
                            if (new_time >= time) {
                                HashSet<Integer> new_set_task = new HashSet<>();
                                new_set_task.addAll(set_task);
                                new_set_task.add(i);
                                double new_profit = now.get(set_task).getFirst() + tasks.get(i).get_benefit_starting_time(original_time);
                                ArrayList<Integer> new_path = new ArrayList<>();
                                if (!next.containsKey(new_set_task)) {
                                    new_path.addAll(now.get(set_task).getThird());
                                    new_path.add(i);
                                    next.put(new_set_task, new Triple(new_profit, new_time, new_path));
                                    if (!to_be_updated.containsKey(new_time)) {
                                        to_be_updated.put(new_time, new HashSet<HashSet<Integer>>());
                                    }
                                    to_be_updated.get(new_time).add(new_set_task);
                                } else {
                                    if (new_profit > next.get(new_set_task).getFirst()) {
                                        new_path.addAll(now.get(set_task).getThird());
                                        new_path.add(i);
                                        next.put(new_set_task, new Triple(new_profit, new_time, new_path));
                                    }
                                }
                                if (new_profit > global_best) {
                                    global_best = new_profit;
                                    global_best_path = new_path;
                                }
                            }

                        }
                    }
                }
                now = next;
                System.out.println(to_be_updated);

                for (int delta = 0; delta < time_interval; delta ++) {
                    System.out.println(delta + time);
                    if (to_be_updated.containsKey(delta + time)) {
                        for (HashSet<Integer> set_task : to_be_updated.get(delta + time)) {
                            for (int i = 0; i < task_num; i++) {
                                if (!set_task.contains(i)) {
                                    int original_time = now.get(set_task).getSecond();
                                    int new_time = original_time + tasks.get(i).get_duration();
                                    if (new_time < time + time_interval) {
                                        HashSet<Integer> new_set_task = new HashSet<>();
                                        new_set_task.addAll(set_task);
                                        new_set_task.add(i);
                                        double new_profit = now.get(set_task).getFirst() + tasks.get(i).get_benefit_starting_time(original_time);
                                        ArrayList<Integer> new_path = new ArrayList<>();
                                        if (!now.containsKey(new_set_task)) {
                                            new_path.addAll(now.get(set_task).getThird());
                                            new_path.add(i);
                                            next.put(new_set_task, new Triple(new_profit, new_time, new_path));
                                        } else {
                                            if (new_profit > now.get(new_set_task).getFirst()) {
                                                new_path.addAll(now.get(set_task).getThird());
                                                new_path.add(i);
                                                now.put(new_set_task, new Triple(new_profit, new_time, new_path));
                                            }
                                        }
                                        if (new_profit > global_best) {
                                            global_best_path = new_path;
                                            global_best = new_profit;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                next = new HashMap<>();
                to_be_updated = new HashMap<>();

            }

            ArrayList<Integer> output = new ArrayList<>();

            for (int task_index : global_best_path) {
                output.add(tasks.get(task_index).get_task_id());
            }

            System.out.println("Best Profit is");
            System.out.println(global_best);
            System.out.println(output);

            return output;

        }
    }

    public static void main(String[] args) throws Exception{
        Path_Record_Solver gof = new Path_Record_Solver();
        Path_Record_Solver.parse p = gof.new parse();
        Path_Record_Solver.solver s = new solver();
        List<Task> tasks = p.read_input_file("samples/100.in");
        List<Integer> output = s.solve(tasks, 1440, 60);
        System.out.println(output);
    }
}