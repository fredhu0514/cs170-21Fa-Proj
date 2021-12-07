package Project;
import java.util.*;
import java.lang.Math;
import java.util.Random;
import java.util.logging.*;
import java.io.File;


public class DPsolverNew {
    Long seed;
    Random generator;

    public DPsolverNew(String seed) {
        this.seed = Long.parseLong(seed);
        this.generator = new Random(Long.parseLong(seed));
    }

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

    private Map<Integer, List<Integer>> SeqHelper(int CurrTime, int TotalTime, List<Integer> CurrSeq, List<Task> available_tasks) {
        // key-value pair: (0, updated CurrSeq), (1, updated CurrTime in form of length-1 list)
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<Task> top_viable_tasks = new ArrayList<>();
        for (Task task: available_tasks) {
            if (CurrTime+task.get_duration() <= TotalTime & task.get_deadline() >= TotalTime) {
                top_viable_tasks.add(task);
            }
        }
        if (top_viable_tasks.size() == 0) {
            map.put(0, CurrSeq);
            map.put(1, new ArrayList<>(Arrays.asList(CurrTime)));
            return map;
        } else {
            SortTasks(top_viable_tasks.subList(0, Math.min(11, top_viable_tasks.size())), CurrTime, TotalTime, "Deadline");
        }
        Task task = top_viable_tasks.get(0);
        available_tasks.remove(task);

        int TotalTimeBeforeThisTask = TotalTime - (CurrTime + task.get_duration());
        Map<Integer, List<Integer>> other_map = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, available_tasks);
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


    /*
    Consider the tasks sorted by deadline, and consider the all-or-nothing profit scenario
    we should prioritize the tasks with earlier deadline
    The recurrence relation is P(i) = max(P(i-1), max{P(j) + benefit(i) for any j < i})
    with i from 1 to n = len(tasks)
     */

    private List<Integer> solve(List<Task> tasks) {
        int n = tasks.size();
        Collections.sort(tasks, new DeadlineSort());
        Map<Integer, Double> ProfitMap = new HashMap<>();
        Map<Integer, List<Integer>> SequenceMap = new HashMap<>();
        Map<Integer, Integer> TimeMap = new HashMap<>(); //records total time

        // Initialize
        ProfitMap.put(0, 0.0);
        SequenceMap.put(0, new ArrayList<>());
        TimeMap.put(0, 0);
        // Recurrence
        for (int i = 1; i <= n; i++) {
            List<Double> OptBenefit = new ArrayList<>();
            OptBenefit.add(ProfitMap.get(i-1));
            List<List<Integer>> OptSeq = new ArrayList<>();
            OptSeq.add(SequenceMap.get(i-1));
            List<Integer> OptTime = new ArrayList<>();
            OptTime.add(TimeMap.get(i-1));
            Task task = tasks.get(i-1);
            for (int j = 0; j <= i-1; j++) {
                double PrevBenefit = ProfitMap.get(j);
                List<Integer> PrevSeq = SequenceMap.get(j);
                int PrevTime = TimeMap.get(j);
                if (PrevTime + task.get_duration() <= Math.min(task.get_deadline(), 1440)) {
                    double CurrBenefit = PrevBenefit + task.get_max_benefit();
                    if (CurrBenefit > OptBenefit.get(0)) {
                        OptBenefit = new ArrayList<>(List.of(CurrBenefit));
                        List<Integer> CurrSeq = new ArrayList<>(PrevSeq);
                        CurrSeq.add(i);
                        OptSeq = new ArrayList<>(List.of(CurrSeq));
                        int CurrTime = PrevTime + task.get_duration();
                        OptTime = new ArrayList<>(List.of(CurrTime));
                    } else if (CurrBenefit == OptBenefit.get(0)) {
                        OptBenefit.add(CurrBenefit);
                        List<Integer> CurrSeq = new ArrayList<>(PrevSeq);
                        CurrSeq.add(i);
                        OptSeq.add(CurrSeq);
                        int CurrTime = PrevTime + task.get_duration();
                        OptTime.add(CurrTime);
                    }
                }
            }
            int k = generator.nextInt(OptBenefit.size());
            ProfitMap.put(i, OptBenefit.get(k));
            SequenceMap.put(i, OptSeq.get(k));
            TimeMap.put(i, OptTime.get(k));
        }
        List<Integer> FinalSeq = new ArrayList<>();
        for (int i: SequenceMap.get(n)) {
            FinalSeq.add(tasks.get(i-1).get_task_id());
        }
        return FinalSeq;
    }

    public List<Integer> FinalSolve(List<Task> tasks) {
        List<Task> tasks_copy = new ArrayList<>(tasks);
        List<Integer> output = new ArrayList<>();
        double benefit = 0;
        for (int k = 0; k < 20000; k++) {
            this.seed += 1;
            this.generator = new Random(this.seed);
            List<Integer> CurrSeq = solve(tasks);
            tasks = tasks_copy;
            tasks_copy = new ArrayList<>(tasks);
            double CurrBenefit = parse.check_output(tasks, CurrSeq);
            if (CurrBenefit > benefit) {
                benefit = CurrBenefit;
                output = CurrSeq;
            }
        }
        // Fill in the rest with greedy
        List<Task> available_tasks = new ArrayList<>(tasks_copy);
        int CurrTime = 0;
        for (int id : output) {
            available_tasks.remove(tasks_copy.get(id-1));
            CurrTime += tasks_copy.get(id-1).get_duration();
        }
        SortTasks(available_tasks, CurrTime, 1440, "Profit");
        List<Integer> CurrSeq = new ArrayList<>();
        while (CurrTime < 1440 & available_tasks.size() > 0) {
            List<Task> top_viable_tasks = new ArrayList<>();
            for (Task task: available_tasks) {
                if (CurrTime+task.get_duration() <= 1440 & task.get_deadline() <= 1440) {
                    top_viable_tasks.add(task);
                }
            }
            if (top_viable_tasks.size() == 0) {
                break;
            }
            Task task = top_viable_tasks.get(0);
            available_tasks.remove(task);


            int TotalTimeBeforeThisTask = task.get_deadline() - (CurrTime + task.get_duration());
            if (TotalTimeBeforeThisTask > 0) {
                Map<Integer, List<Integer>> other_map = SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, available_tasks);
                CurrSeq = other_map.get(0);
                CurrTime = other_map.get(1).get(0);
            }
            CurrSeq.add(task.get_task_id());
            CurrTime += task.get_duration();
        }
        output.addAll(CurrSeq);
        return output;
    }

    public static void main(String[] args) throws Exception{
        DPsolverNew ds = new DPsolverNew("3034558112");
        Logger logger = Logger.getLogger("DPJavaNew");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("DPJavaNew.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            File outputFolder = new File("OutputDPNew/");
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }
            Map<String, List<Double>> benefits = new HashMap<>();
            String[] keys = new String[]{"large", "medium", "small"};
            for (String key:keys) {
                benefits.put(key, new ArrayList<>());
            }
            for (String key:keys) {
                File currFolder = new File("OutputDPNew/" + key + "/");
                if (!currFolder.exists()) {
                    currFolder.mkdir();
                }
                logger.info(key+":");
                File inputFolder = new File("Project/inputs/" + key + "/");
                for (File input_path: inputFolder.listFiles()) {
                    String filename = input_path.getName();
                    if (filename.endsWith("in")) {
                        String outputPath = "OutputDPNew/" + key + "/" + filename.substring(0, filename.length()-2) + "out";
                        if (new File(outputPath).exists()) {
                            continue;
                        }
                        logger.info(filename);
                        List<Task> tasks = parse.read_input_file("Project/inputs/" + key + "/" + filename);
                        List<Task> tasks_copy = new ArrayList<>(tasks);
                        List<Integer> output = ds.FinalSolve(tasks);
                        logger.info("Writing output file for " + input_path + "...");
                        parse.write_output_file(outputPath, output);
                        double profit = parse.check_output(tasks_copy, output);
                        List<Integer> best_output = parse.read_output_file("OutputsGeneticNew/" + key + "/" + filename.substring(0, filename.length()-2) + "out");
                        double best_profit = parse.check_output(tasks_copy, best_output);
                        if (profit > best_profit) {
                            System.out.println(input_path);
                            System.out.println(best_profit);
                            System.out.println(profit);
                            System.out.println("Improvements!");
                        }
                        logger.info("Profit: " + Double.toString(profit));
                        benefits.get(key).add(profit);
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
