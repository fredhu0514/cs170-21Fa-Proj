package Project;
import java.util.*;
import java.lang.Math;
import java.util.Random;
import java.util.logging.*;
import java.io.File;


public class GreedySolverOld {

    private class ProfitSort implements Comparator<Task>
    {
        // Used for sorting in descending order of
        // profit/duration
        public int compare(Task a, Task b)
        {
            double diff = a.get_max_benefit()/a.get_duration() - b.get_max_benefit()/b.get_duration();
            if (diff < 0) {
                return 1;
            }
            if (diff > 0) {
                return -1;
            }
            return 0;
        }
    }

    private class DeadlineSort implements Comparator<Task>
    {
        // Used for sorting in ascending order of
        // profit/duration
        public int compare(Task a, Task b)
        {
            return a.get_deadline() - b.get_deadline();
        }
    }

    private void SortTasks(List<Task> tasks, int CurrTime, int TotalTime, String By) {
        if (By.equals("Profit")) {
            Collections.sort(tasks, new ProfitSort());
        } else if (By.equals("Deadline")) {
            Collections.sort(tasks, new DeadlineSort());
        }
    }

    private Map<Integer, List<Integer>> SeqHelper(int CurrTime, int TotalTime, List<Integer> CurrSeq, List<Task> tasks, int max_n, Random generator) {
        // key-value pair: (0, updated CurrSeq), (1, updated CurrTime in form of length-1 list)
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<Task> top_viable_tasks = new ArrayList<>();
        for (Task task: tasks) {
            if (CurrTime+task.get_duration() <= TotalTime & task.get_deadline() >= TotalTime) {
                top_viable_tasks.add(task);
            }
        }
        if (top_viable_tasks.size() == 0) {
            map.put(0, CurrSeq);
            map.put(1, new ArrayList<>(Arrays.asList(CurrTime)));
            return map;
        } else {
            int n = generator.nextInt(max_n-1);
            SortTasks(top_viable_tasks.subList(0, Math.min(n+1, top_viable_tasks.size())), CurrTime, TotalTime, "Deadline");
        }
        Task task = top_viable_tasks.get(0);
        int prevSize = tasks.size();
        tasks.remove(task);
        assert (prevSize == tasks.size() + 1): "Error during removal process";

        int TotalTimeBeforeThisTask = TotalTime - (CurrTime + task.get_duration());
        Map<Integer, List<Integer>> other_map = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks, max_n, generator);
        CurrSeq = other_map.get(0);
        CurrTime = other_map.get(1).get(0);
        assert (CurrTime <= TotalTime): "SeqHelper Recursion Error";
        CurrSeq.add(task.get_task_id());
        CurrTime += task.get_duration();
        assert CurrTime <= TotalTime;
        map.put(0, CurrSeq);
        map.put(1, new ArrayList<>(Arrays.asList(CurrTime)));
        return map;
    }

    private List<Integer> solve(List<Task> tasks, Integer max_n, Random generator) {
        SortTasks(tasks, 0, 1440, "Profit");
        List<Integer> CurrSeq = new ArrayList<>();
        int CurrTime = 0;
        while (CurrTime < 1440 & tasks.size() > 0) {
            List<Task> top_viable_tasks = new ArrayList<>();
            for (Task task: tasks) {
                if (CurrTime+task.get_duration() <= 1440 & task.get_deadline() <= 1440) {
                    top_viable_tasks.add(task);
                }
            }
            if (top_viable_tasks.size() == 0) {
                break;
            }
            Task task = top_viable_tasks.get(0);
            int prevSize = tasks.size();
            tasks.remove(task);
            assert (prevSize == tasks.size() + 1): "Error during removal process";

            int TotalTimeBeforeThisTask = task.get_deadline() - (CurrTime + task.get_duration());
            if (TotalTimeBeforeThisTask > 0) {
                Map<Integer, List<Integer>> other_map = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, tasks, max_n, generator);
                CurrSeq = other_map.get(0);
                CurrTime = other_map.get(1).get(0);
            }
            CurrSeq.add(task.get_task_id());
            CurrTime += task.get_duration();
        }
        return CurrSeq;
    }

    private Map<Integer, List<Double>> solve_iter(List<Task> tasks, Integer max_n, long seed) {
        Random generator = new Random(seed);
        List<Task> tasks_copy = new ArrayList<>(tasks);
        List<Double> OptSeq = new ArrayList<>();
        double OptBenefit = 0;
        for (int i = 0; i < max_n*5000; i++) {
            List<Integer> temp_output = solve(tasks, max_n, generator);
            List<Double> output = new ArrayList<>();
            for (int id:temp_output) {
                output.add((double) id);
            }
            tasks = tasks_copy;
            tasks_copy = new ArrayList<>(tasks);
            double benefit = parse.check_output(tasks, temp_output);
            if (benefit > OptBenefit) {
                OptBenefit = benefit;
                OptSeq = output;
            }
        }
        Map<Integer, List<Double>> map = new HashMap<>();
        map.put(0, OptSeq);
        map.put(1, new ArrayList<>(Arrays.asList(OptBenefit)));
        return map;
    }

    public static void main(String[] args) throws Exception{
        GreedySolverOld gs = new GreedySolverOld();
        Logger logger = Logger.getLogger("GreedySolverJava");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("GreedySolverJava.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            File outputFolder = new File("OutputGreedyOld/");
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }
            Map<String, List<Double>> benefits = new HashMap<>();
            String[] keys = new String[]{"large", "medium", "small"};
            for (String key:keys) {
                benefits.put(key, new ArrayList<>());
            }
            for (String key:keys) {
                File currFolder = new File("OutputGreedyOld/" + key + "/");
                if (!currFolder.exists()) {
                    currFolder.mkdir();
                }
                logger.info(key+":");
                File inputFolder = new File("Project/inputs/" + key + "/");
                for (File input_path: inputFolder.listFiles()) {
                    String filename = input_path.getName();
                    if (filename.endsWith("in")) {
                        String outputPath = "OutputGreedyOld/" + key + "/" + filename.substring(0, filename.length()-2) + "out";
                        if (new File(outputPath).exists()) {
                            continue;
                        }
                        logger.info(filename);
                        List<Task> tasks = parse.read_input_file("Project/inputs/" + key + "/" + filename);
                        Map<Integer, List<Double>> map = gs.solve_iter(tasks, 20, Long.parseLong("3034558112"));
                        logger.info("Writing output file for " + input_path + "...");
                        List<Double> temp_output = map.get(0);
                        List<Integer> output = new ArrayList<>();
                        for (double id:temp_output) {
                            output.add((int) id);
                        }
                        parse.write_output_file(outputPath, output);
                        assert parse.check_output(tasks, output) == map.get(1).get(0);
                        logger.info("Profit: " + Double.toString(map.get(1).get(0)));
                        benefits.get(key).add(map.get(1).get(0));
                    }
                }

            }
            for (String key:keys) {
                double mean_benefit = benefits.get(key).stream().reduce((a,b)->a+b).get();
                mean_benefit = mean_benefit / benefits.get(key).size();
                logger.info(key + ": " + Double.toString(mean_benefit));
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }
}
