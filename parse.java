import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
