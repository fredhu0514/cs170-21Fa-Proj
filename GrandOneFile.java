import java.lang.Math;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class GrandOneFile {

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
            List<Task> tasks = new ArrayList<>();

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

    public class solver {

        public List<Integer> solve(List<Task> tasks) {
        /*
        Idea: DP solver as in TSP
        Recurrence Relation: f(set, i) = f(set - {i}, j) + benefits(i, time spent so far)
        Base case: f([i], i) = benefits by completing i
         */
            List<Double> OptSeq = new ArrayList<>();
            double OptBenefit = 0;
            int n = tasks.size();
            List<List<Integer>> allCombinations = AllCombinations(n);
            Map<List<Integer>, List<Double>> map = new HashMap<>();
            /**
             * key: [S, id]
             * value: [CurrTime, CurrBenefit, path]
             */
            for (List<Integer> comb : allCombinations) {
                if (comb.size() == 2) {
                    // [id, id], only one task is chosen and completed
                    Task task = tasks.get(1);
                    List<Double> currKeyValue = new ArrayList<>();
                    currKeyValue.add((double) task.get_duration());
                    currKeyValue.add(task.get_late_benefit(task.get_duration() - task.get_deadline()));
                    currKeyValue.add((double) task.get_task_id());
                    map.put(comb, currKeyValue);
                    continue;
                }
                int i = comb.get(comb.size() - 1);
                Task task = tasks.get(i);
                List<Integer> prevPath = new ArrayList<>(); // get S - {i}
                for (int j = 0; j < comb.size() - 1; j++) {
                    if (comb.get(j) != i) {
                        prevPath.add(j);
                    }
                }
                List<List<Integer>> PossiblePrevCombs = new ArrayList<>(); // get all possible [S - {i} - {j}, j]
                for (int id : prevPath) {
                    List<Integer> prevCombs = new ArrayList<>();
                    prevCombs.addAll(prevPath);
                    prevCombs.add(id);
                }
                double prevTime = map.get(PossiblePrevCombs.get(0)).get(0);
                double currTime = prevTime + task.get_duration();
                if (currTime > 1440) {
                    List<Double> currKeyValue = new ArrayList<>();
                    currKeyValue.add(currTime);
                    map.put(comb, currKeyValue); // add the time to map so that there is no error in later iterations
                    break;
                } else {
                    List<Double> bestPrevPath = new ArrayList<>();
                    double bestPrevBenefit = 0;
                    for (List<Integer> prevComb : PossiblePrevCombs) {
                        List<Double> mapValue = map.get(prevComb);
                        if (mapValue.get(1) > bestPrevBenefit) {
                            bestPrevBenefit = mapValue.get(1);
                            bestPrevPath = mapValue.subList(2, mapValue.size());
                        }
                    }
                    double currBenefit = bestPrevBenefit + task.get_late_benefit((int) currTime - task.get_deadline());
                    List<Double> currPath = new ArrayList<>();
                    currPath.addAll(bestPrevPath);
                    currPath.add((double) i);
                    if (currBenefit > OptBenefit) {
                        OptBenefit = currBenefit;
                        OptSeq = currPath;
                    }
                    List<Double> currKeyValue = new ArrayList<>();
                    currKeyValue.add(currTime);
                    currKeyValue.add(currBenefit);
                    currKeyValue.addAll(currPath);
                    map.put(comb, currKeyValue);
                }
            }
            List<Integer> task_ids = new ArrayList<>();
            for (double id : OptSeq) {
                task_ids.add((int) id);
            }
            return task_ids;

        }

        public List<int[]> PowerList(int n) {
        /*
        returns Powerset of 1,2,...,n in list form
        ordered by the length of list
         */
            List<int[]> list = new ArrayList<>();
            List<int[]> temp = new ArrayList<>();
            for (int i = 1; i <= n; i++) {
                temp.add(new int[]{i});
            }
            list.addAll(temp);
            for (int i = 2; i <= n; i++) {
                List<int[]> newTemp = new ArrayList<>();
                for (int[] x : temp) {
                    for (int j = x[i - 2] + 1; j <= n; j++) { // j must be larger than the last element of x
                        boolean contains = false;
                        for (int k = 0; k < i - 1; k++) { //x.length = i-1
                            if (x[k] == j) {
                                contains = true;
                                break;
                            }
                        }
                        if (!contains) {
                            int[] newX = new int[i];
                            for (int k = 0; k < i - 1; k++) {
                                newX[k] = x[k];
                            }
                            newX[i - 1] = j;
                            newTemp.add(newX);
                        }
                    }
                }
                temp = newTemp;
                list.addAll(temp);
            }
            return list;
        }

        public List<List<Integer>> AllCombinations(int n) {
            /*
             *  returns [S, id] for all S in PowerList and id in S
             * */
            List<List<Integer>> list = new ArrayList<>();
            List<int[]> powerlist = PowerList(n);
            for (int[] lst : powerlist) {
                for (int x : lst) {
                    List<Integer> newLst = new ArrayList<>();
                    for (int i = 0; i < lst.length; i++) {
                        newLst.add(lst[i]);
                    }
                    newLst.add(x);
                    list.add(newLst);
                }
            }
            return list;

        }
    }

    public static void main(String[] args) throws Exception{
        GrandOneFile gof = new GrandOneFile();
        GrandOneFile.parse p = gof.new parse();
        GrandOneFile.solver s = gof.new solver();
        List<Task> tasks = p.read_input_file("Project/inputs/small/small-1.in");
        List<Integer> output = s.solve(tasks);
        System.out.println(output);
    }
}