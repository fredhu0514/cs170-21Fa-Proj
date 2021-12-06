import java.lang.Math;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;

public class GrandOneFile {

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

        private int get_max_tasks(List<Task> tasks) {
            int max = 0;
            int total = 0;
            ArrayList<Integer>  durations = new ArrayList<>();
            for (Task task : tasks) {
                durations.add(task.get_duration());
            }
            durations.sort(Integer::compareTo);

            while (total < 1440) {
                total += durations.get(max);
                max += 1;
            }
            return max;
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
            int max_num = get_max_tasks(tasks);
            int task_num = tasks.size();

            HashMap<Integer, ArrayList<Double>> prev_profits = new HashMap<Integer, ArrayList<Double>>();
            HashMap<Integer, ArrayList<ArrayList<Integer>>> prev_paths_list = new  HashMap<Integer, ArrayList<ArrayList<Integer>>>();
            HashMap<Integer, ArrayList<HashSet<Integer>>> prev_paths_set = new HashMap<Integer, ArrayList<HashSet<Integer>>>();
            HashMap<Integer, HashSet<Integer>> prev_possible_t = new HashMap<Integer, HashSet<Integer>>();

            ArrayList<Double> starter_profit = new ArrayList<Double>();
            ArrayList<ArrayList<Integer>> starter_path_list = new ArrayList<ArrayList<Integer>>();
            ArrayList<HashSet<Integer>> starter_path_set = new ArrayList<HashSet<Integer>>();

            initialize(task_num, starter_profit, starter_path_list, starter_path_set, true);

            prev_profits.put(-1, starter_profit);
            prev_paths_list.put(-1, starter_path_list);
            prev_paths_set.put(-1, starter_path_set);
            prev_possible_t.put(0, new HashSet(List.of(-1)));

            HashMap<Integer, ArrayList<Double>> new_profits = new HashMap<Integer, ArrayList<Double>>();
            HashMap<Integer, ArrayList<ArrayList<Integer>>> new_paths_list = new  HashMap<Integer, ArrayList<ArrayList<Integer>>>();
            HashMap<Integer, ArrayList<HashSet<Integer>>> new_paths_set = new HashMap<Integer, ArrayList<HashSet<Integer>>>();
            HashMap<Integer, HashSet<Integer>> new_possible_t = new HashMap<Integer, HashSet<Integer>>();

            double global_max = Double.NEGATIVE_INFINITY;
            List<Integer> global_best_path = null;
            long start = System.currentTimeMillis();
            for (int t = 1; t < Max_time; t += time_interval) {
                System.out.print(t);
                System.out.println("last epoch:");
                long here = System.currentTimeMillis();
                System.out.print((here - start)/1000);
                start = here;
                for (int delta = 0; delta < 60; delta ++) {
/*
                    System.out.println(t + delta);
                    System.out.println(prev_paths_list);
                    System.out.println(new_paths_list);

                     */
                    for (int i = 0; i < tasks.size(); i++) {
                        int current_time = t + delta;
                        int starting_time = current_time - tasks.get(i).get_duration();
                        if (starting_time < t) {
                            if (prev_possible_t.containsKey(starting_time)) {
                                int two_d_index = get_index(i, delta);

                                ArrayList<Double> this_profit = new ArrayList<Double>();
                                ArrayList<ArrayList<Integer>> this_path_list = new ArrayList<ArrayList<Integer>>();
                                ArrayList<HashSet<Integer>> this_path_set = new ArrayList<HashSet<Integer>>();

                                initialize(task_num, this_profit, this_path_list, this_path_set, false);

                                new_profits.put(two_d_index, this_profit);
                                new_paths_list.put(two_d_index, this_path_list);
                                new_paths_set.put(two_d_index, this_path_set);

                                Boolean actually_put = false;
                                for (int predecessor_task_index : prev_possible_t.get(starting_time)) {
                                    int predecessor_two_d_index = get_index(predecessor_task_index, starting_time - t + 60);
                                    for (int k = 0; k < task_num; k++) {
                                        Set path_set = prev_paths_set.get(predecessor_two_d_index).get(k);
                                        ArrayList path_list = prev_paths_list.get(predecessor_two_d_index).get(k);
                                        double profit = prev_profits.get(predecessor_two_d_index).get(k);

                                        double this_new_profit = profit + tasks.get(i).get_benefit_starting_time(starting_time);
                                        if (!path_set.contains(i) && this_new_profit > this_profit.get(k)) {
                                            HashSet<Integer> single_new_set = new HashSet<Integer>();
                                            single_new_set.addAll(path_set);
                                            single_new_set.add(i);

                                            ArrayList<Integer> single_new_list = new ArrayList<Integer>();
                                            single_new_list.addAll(path_list);
                                            single_new_list.add(i);

                                            if (k != i) {
                                                actually_put = true;
                                                this_profit.set(k, this_new_profit);
                                                this_path_list.set(k, single_new_list);
                                                this_path_set.set(k, single_new_set);
                                            }

                                            if (this_new_profit > global_max) {
                                                global_max = this_new_profit;
                                                global_best_path = single_new_list;
                                            }
                                        }
                                    }
                                }
                                if (!actually_put) {
                                    new_profits.remove(two_d_index);
                                    new_paths_list.remove(two_d_index);
                                    new_paths_set.remove(two_d_index);
                                } else {
                                    if (!new_possible_t.containsKey(current_time)) {
                                        new_possible_t.put(current_time, new HashSet<Integer>());
                                    }
                                    new_possible_t.get(current_time).add(i);
                                }
                            }
                        } else {
                            if (new_possible_t.containsKey(starting_time)) {
                                int two_d_index = get_index(i, delta);

                                ArrayList<Double> this_profit = new ArrayList<Double>();
                                ArrayList<ArrayList<Integer>> this_path_list = new ArrayList<ArrayList<Integer>>();
                                ArrayList<HashSet<Integer>> this_path_set = new ArrayList<HashSet<Integer>>();

                                initialize(task_num, this_profit, this_path_list, this_path_set, false);

                                new_profits.put(two_d_index, this_profit);
                                new_paths_list.put(two_d_index, this_path_list);
                                new_paths_set.put(two_d_index, this_path_set);

                                Boolean actually_put = false;
                                for (int predecessor_task_index : new_possible_t.get(starting_time)) {
                                    int predecessor_two_d_index = get_index(predecessor_task_index, starting_time - t);
                                    for (int k = 0; k < new_paths_set.get(predecessor_two_d_index).size(); k++) {
                                        Set path_set = new_paths_set.get(predecessor_two_d_index).get(k);
                                        ArrayList path_list = new_paths_list.get(predecessor_two_d_index).get(k);
                                        double profit = new_profits.get(predecessor_two_d_index).get(k);

                                        double this_new_profit = profit + tasks.get(i).get_benefit_starting_time(starting_time);
                                        if (!path_set.contains(i) && this_new_profit > this_profit.get(k)) {
                                            HashSet<Integer> single_new_set = new HashSet<Integer>();
                                            single_new_set.addAll(path_set);
                                            single_new_set.add(i);

                                            ArrayList<Integer> single_new_list = new ArrayList<Integer>();
                                            single_new_list.addAll(path_list);
                                            single_new_list.add(i);

                                            if (k != i) {
                                                actually_put = true;
                                                this_profit.set(k, this_new_profit);
                                                this_path_list.set(k, single_new_list);
                                                this_path_set.set(k, single_new_set);
                                            }

                                            if (this_new_profit > global_max) {
                                                global_max = this_new_profit;
                                                global_best_path = single_new_list;
                                            }
                                        }
                                    }
                                }
                                if (!actually_put) {
                                    new_profits.remove(two_d_index);
                                    new_paths_list.remove(two_d_index);
                                    new_paths_set.remove(two_d_index);
                                } else {
                                    if (!new_possible_t.containsKey(current_time)) {
                                        new_possible_t.put(current_time, new HashSet<Integer>());
                                    }
                                    new_possible_t.get(current_time).add(i);
                                }
                            }
                        }
                    }
                }

                for (int j = 0; j < max_num - 1; j ++) {
                    ///"to update the choices more time"
                    for (int d = 0; d < 60; d ++) {
                /*
                System.out.println(prev_paths_list);
                System.out.println(new_paths_list);

                 */
                        for (int i = 0; i < task_num; i ++) {
                            int current_time = t + d;
                            int starting_time = current_time - tasks.get(i).get_duration();
                            int two_d_index = get_index(i, d);
                            if (new_profits.containsKey(two_d_index) && new_possible_t.containsKey(starting_time)) {
                                ArrayList<Double> this_profit = new_profits.get(two_d_index);
                                ArrayList<ArrayList<Integer>> this_path_list = new_paths_list.get(two_d_index);
                                ArrayList<HashSet<Integer>> this_path_set = new_paths_set.get(two_d_index);

                                for (int predecessor_task_index: new_possible_t.get(starting_time)) {
                                    int predecessor_two_d_index = get_index(predecessor_task_index, starting_time - t);
                                    for (int k = 0; k < new_paths_set.get(predecessor_two_d_index).size(); k ++) {
                                        Set path_set = new_paths_set.get(predecessor_two_d_index).get(k);
                                        ArrayList path_list = new_paths_list.get(predecessor_two_d_index).get(k);
                                        double profit = new_profits.get(predecessor_two_d_index).get(k);
                                        double this_new_profit = profit + tasks.get(i).get_benefit_starting_time(starting_time);
                                        if (!path_set.contains(i) && this_new_profit > this_profit.get(k)) {
                                            HashSet<Integer> single_new_set = new HashSet<Integer>();
                                            single_new_set.addAll(path_set);
                                            single_new_set.add(i);
                                            //System.out.println("here");
                                            ArrayList<Integer> single_new_list = new ArrayList<Integer>();
                                            single_new_list.addAll(path_list);
                                            single_new_list.add(i);

                                            System.out.println(i);
                                            System.out.println(k);
                                            System.out.println(this_new_profit);
                                            System.out.println(this_profit.get(k));
                                            System.out.println(single_new_list);
                                            System.out.println(this_profit);
                                            System.out.println(this_path_list);

                                            if (k != i) {
                                                System.out.println("here");
                                                this_profit.set(k, this_new_profit);
                                                this_path_list.set(k, single_new_list);
                                                this_path_set.set(k, single_new_set);
                                            }


                                            if (this_new_profit > global_max) {
                                                global_max = this_new_profit;
                                                global_best_path = single_new_list;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                prev_profits = new_profits;
                prev_paths_list = new_paths_list;
                prev_paths_set = new_paths_set;
                prev_possible_t = new_possible_t;

                new_profits = new HashMap<Integer, ArrayList<Double>>();
                new_paths_list = new  HashMap<Integer, ArrayList<ArrayList<Integer>>>();
                new_paths_set = new HashMap<Integer, ArrayList<HashSet<Integer>>>();
                new_possible_t = new HashMap<Integer, HashSet<Integer>>();
            }
            System.out.print("best path is");
            System.out.print(global_max);

            ArrayList<Integer> output = new ArrayList<>();
            for (int i: global_best_path) {
                output.add(tasks.get(i).get_task_id());
            }

            return output;
        }
    }

    public static void main(String[] args) throws Exception{
        GrandOneFile gof = new GrandOneFile();
        GrandOneFile.parse p = gof.new parse();
        GrandOneFile.solver s = new solver();
        List<Task> tasks = p.read_input_file("samples/100.in");
        List<Integer> output = s.solve(tasks, 1440, 60);
        System.out.println(output);
    }
}