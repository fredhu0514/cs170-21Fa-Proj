package Project;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.lang.Math;
import java.util.logging.*;
import java.io.File;

public class GeneticSolver {

    Random generator;
    public GeneticSolver(long seed){
        this.generator = new Random(seed);
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

    private class FitnessSort implements Comparator<List<Integer>> {
        // Used for sorting in descending order of
        // total profit
        private final List<Task> tasks;
        public FitnessSort(List<Task> tasks) {
            this.tasks = tasks;
        }
        public int compare(List<Integer> output1, List<Integer> output2) {
            double diff = fitness(output1, this.tasks) - fitness(output2, this.tasks);
            if (diff < 0) {
                return -1;
            }
            if (diff > 0) {
                return 1;
            }
            return 0;
        }
    }


    public int length(List<List<Integer>> outputs) {
        int len = 0;
        for (int i = 0; i < outputs.size(); i++) {
            len += outputs.get(i).size();
        }
        return len;
    }

    public int[] get(List<List<Integer>> outputs, int i) {
        assert (i >= 0 & i < length(outputs)): "Invalid index";
        int list_index = 0;
        int num_index_in_list = 0;
        int j = 0;
        while (j < i) {
            if (num_index_in_list == outputs.get(list_index).size()-1) {
                num_index_in_list = 0;
                list_index += 1;
            } else {
                num_index_in_list += 1;
            }
            j += 1;
        }
        return new int[]{list_index, num_index_in_list};
    }

    public double fitness(List<Integer> output, List<Task> tasks) {
        int TotalTime = 0;
        for (int id: output) {
            TotalTime += tasks.get(id-1).get_duration();
        }
        if (TotalTime > 1440 | new HashSet<>(output).size() < output.size()) {
            return 0;
        } else {
            return parse.check_output(tasks, output);
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

    public List<List<Integer>> genetic_cycle(List<Task> tasks, List<List<Integer>> parents, double crossover_prob, double point_mutation_prob, double second_mutation_prob, double deletion_prob, double swap_prob, int capacity) {

        int n = tasks.size();

        List<List<Integer>> offsprings = new ArrayList<>();
        for (List<Integer> chromo: parents) {
            offsprings.add(new ArrayList<>(chromo));
        }

        // crossover
        int num_crossovers =  (int) Math.round(offsprings.size() * crossover_prob);
        for (int l = 0; l < num_crossovers; l++) {
            int index1 = generator.nextInt(parents.size());
            int index2 = generator.nextInt(parents.size());
            while (index2 == index1) {
                index2 = generator.nextInt(parents.size());
            }
            List<Integer> chromo1 = parents.get(index1);
            List<Integer> chromo2 = parents.get(index2);
            int crossover_index = generator.nextInt(Math.min(chromo1.size(), chromo2.size())-1);
            List<Integer> offspring_chromo1 = new ArrayList<>(chromo1.subList(0,crossover_index+1));
            offspring_chromo1.addAll(chromo2.subList(crossover_index+1, chromo2.size()));
            offsprings.add(offspring_chromo1);
            List<Integer> offspring_chromo2 = new ArrayList<>(chromo2.subList(0,crossover_index+1));
            offspring_chromo2.addAll(chromo1.subList(crossover_index+1, chromo1.size()));
            offsprings.add(offspring_chromo2);
        }

        // point mutation
        int len_offsprings = length(offsprings);
        int num_point_mutations = (int) Math.round(len_offsprings * point_mutation_prob);
        for (int l = 0; l < num_point_mutations; l++) {
            int list_index = generator.nextInt(offsprings.size());
            List<Integer> chromo = offsprings.get(list_index);
            if (chromo.size() == 0) {
                offsprings.remove(chromo);
                continue;
            }
            int num_index_in_list = generator.nextInt(chromo.size());
            if (chromo.size() < n) {
                List<Integer> available_task_ids = new ArrayList<>();
                for (int j = 1; j < n + 1; j++) {
                    available_task_ids.add(j);
                }
                for (int id : chromo) {
                    int index = available_task_ids.indexOf(id);
                    if (index != -1) {
                        available_task_ids.remove(index);
                    }
                }
                int j = generator.nextInt(available_task_ids.size());
                chromo.set(num_index_in_list, available_task_ids.get(j));
            } else {
                //offsprings.remove(chromo); // if chromo has all tasks, remove it
            }
        }

        // deletion
        len_offsprings = length(offsprings);
        int num_deletions = (int) Math.round(len_offsprings * deletion_prob);
        for (int l = 0; l < num_deletions; l++) {
            int list_index = generator.nextInt(offsprings.size());
            List<Integer> chromo = offsprings.get(list_index);
            if (chromo.size() == 0) {
                offsprings.remove(chromo);
                continue;
            }
            int num_index_in_list = generator.nextInt(chromo.size());
            chromo.remove(num_index_in_list);

        }
        //second point mutation after deletion
        len_offsprings = length(offsprings);
        int num_insertions = (int) Math.round(len_offsprings * second_mutation_prob);
        for (int l = 0; l < num_insertions; l++) {
            int list_index = generator.nextInt(offsprings.size());
            List<Integer> chromo = offsprings.get(list_index);
            if (chromo.size() == 0) {
                offsprings.remove(chromo);
                continue;
            }
            int num_index_in_list = generator.nextInt(chromo.size());
            if (chromo.size() < n) {
                List<Integer> available_task_ids = new ArrayList<>();
                for (int j = 1; j < n+1; j++) {
                    available_task_ids.add(j);
                }
                for (int id: chromo) {
                    int index = available_task_ids.indexOf(id);
                    if (index != -1) {
                        available_task_ids.remove(index);
                    }
                }
                int j = generator.nextInt(available_task_ids.size());
                chromo.set(num_index_in_list, available_task_ids.get(j));
            }
        }

        // swap operation
        len_offsprings = length(offsprings);
        int num_swaps = (int) Math.round(len_offsprings * swap_prob);
        for (int l = 0; l < num_swaps; l++) {
            int list_index = generator.nextInt(offsprings.size());
            List<Integer> chromo = offsprings.get(list_index);
            if (chromo.size() == 0) {
                offsprings.remove(chromo);
                continue;
            }
            int num_index1_in_list = generator.nextInt(chromo.size());
            int num_index2_in_list = generator.nextInt(chromo.size());
            int num1 = chromo.get(num_index1_in_list);
            int num2 = chromo.get(num_index2_in_list);
            chromo.set(num_index1_in_list, num2);
            chromo.set(num_index2_in_list, num1);
        }
        Collections.sort(offsprings, new FitnessSort(tasks));
        while (offsprings.size() > capacity) {
            offsprings.remove(0);
        }
        return offsprings;

    }

    public List<Integer> solve(List<Task> tasks, List<List<Integer>> initial_outputs, double crossover_prob, double point_mutation_prob, double second_mutation_prob, double deletion_prob, double swap_prob, int capacity, int threshold) {

        int stuck_cycles = 0;
        List<List<Integer>> parents = initial_outputs;
        Collections.sort(parents, new FitnessSort(tasks));
        List<Integer> best_output = parents.get(parents.size()-1);
        double best_benefit = fitness(best_output, tasks);

        while (stuck_cycles < threshold) {
            List<List<Integer>> offsprings = genetic_cycle(tasks, parents, crossover_prob, point_mutation_prob, second_mutation_prob, deletion_prob, swap_prob, capacity);
            List<Integer> curr_best_output = offsprings.get(offsprings.size()-1);
            double curr_best_benefit = fitness(curr_best_output, tasks);
            double diff = curr_best_benefit - best_benefit;
            if (curr_best_benefit > best_benefit) {
                best_benefit = curr_best_benefit;
                best_output = curr_best_output;
            }
            if (diff > 0.1) {
                stuck_cycles = 0; // reset stuck_cycles
            } else {
                stuck_cycles += 1;
            }
            parents = offsprings;
        }
        //System.out.println(best_benefit);
        return best_output;
    }

    public static List<Integer> solveInputs(String input_path, List<String> UnImprovables) throws Exception{
        List<Task> tasks = parse.read_input_file("Project/inputs/" + input_path);
        List<Integer> output0 = parse.read_output_file("Project/outputs/" + input_path.substring(0, input_path.length()-2)+"out"); // java greedy solver (not new)
        List<Integer> output1 = parse.read_output_file("Project/outputs1/" + input_path.substring(0, input_path.length()-2)+"out");
        List<Integer> output2 = parse.read_output_file("Project/outputs2/" + input_path.substring(0, input_path.length()-2)+"out");
        List<Integer> output3 = parse.read_output_file("Project/outputs3/" + input_path.substring(0, input_path.length()-2)+"out");
        List<Integer> output4 = parse.read_output_file("Project/outputs4/" + input_path.substring(0, input_path.length()-2)+"out");
        List<List<Integer>> initial_outputs = new ArrayList<>(List.of(output0, output1, output2, output3, output4));
        List<Integer> output = new ArrayList<>();
        List<List<Integer>> new_outputs = new ArrayList<>();
        for (int i = 3000001; i <= 3000050; i++) {
            output = new GeneticSolver(i).solve(tasks, initial_outputs, 0.5, 0.10, 0.05, 0.02, 0.02, 30, 100);
            new_outputs.add(output);
        }
        GeneticSolver GrandSolver = new GeneticSolver(0);
        Collections.sort(new_outputs, GrandSolver.new FitnessSort(tasks));
        new_outputs = new_outputs.subList(40, 50);
        if (new_outputs.get(0).equals(new_outputs.get(9))) { // second chance
            new_outputs = new ArrayList<>();
            for (int i = 1000001; i <= 1000050; i++) {
                output = new GeneticSolver(i).solve(tasks, initial_outputs, 0.6, 0.20, 0.10, 0.02, 0.02, 30, 100);
                new_outputs.add(output);
            }
        }
        if (new_outputs.get(0).equals(new_outputs.get(9))) {
            String output_path = "OutputsGeneticNew/" + input_path.substring(0, input_path.length()-2)+"out";
            parse.write_output_file(output_path, output);
            UnImprovables.add(input_path);
            return output;
        }
        for (int c = 1; c <= 50; c++) {
            for (int i = 1; i <= 50; i++) {
                output = new GeneticSolver(i).solve(tasks, new_outputs, 0.5, 0.10, 0.05, 0.02, 0.05, 30, 100);
                new_outputs.add(output);
                while (new_outputs.size() > 50) {
                    new_outputs.remove(0);
                }
            }

            if (new_outputs.get(0).equals(new_outputs.get(new_outputs.size()-1))) {
                break;
            }

        }
        // Fill up the remainder tasks by Greedy
        List<Task> available_tasks = new ArrayList<>(tasks);
        int CurrTime = 0;
        for (int id : output) {
            available_tasks.remove(tasks.get(id-1));
            CurrTime += tasks.get(id-1).get_duration();
        }

        GrandSolver.SortTasks(available_tasks, CurrTime, 1440, "Profit");
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
                Map<Integer, List<Integer>> other_map = GrandSolver.SeqHelper(CurrTime, CurrTime + TotalTimeBeforeThisTask, CurrSeq, available_tasks);
                CurrSeq = other_map.get(0);
                CurrTime = other_map.get(1).get(0);
            }
            CurrSeq.add(task.get_task_id());
            CurrTime += task.get_duration();
        }
        output.addAll(CurrSeq);
        String output_path = "OutputsGeneticNew/" + input_path.substring(0, input_path.length()-2)+"out";
        parse.write_output_file(output_path, output);
        return output;

    }



    public static void main(String[] args) throws Exception{

        Logger logger = Logger.getLogger("GeneticSolverJava");
        FileHandler fh;
        try {
            // This block configure the logger with handler and formatter
            fh = new FileHandler("GeneticSolverJava.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            File outputFolder = new File("OutputsGeneticNew/");
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }
            Map<String, List<Double>> benefits = new HashMap<>();
            List<String> UnImprovable = new ArrayList<>();
            String[] keys = new String[]{"small", "medium", "large"};
            for (String key:keys) {
                benefits.put(key, new ArrayList<>());
            }
            for (String key:keys) {
                File currFolder = new File("OutputsGeneticNew/" + key + "/");
                if (!currFolder.exists()) {
                    currFolder.mkdir();
                }
                logger.info(key+":");
                File inputFolder = new File("Project/inputs/" + key + "/");
                for (File input_path: inputFolder.listFiles()) {
                    String filename = input_path.getName();
                    if (filename.endsWith("in")) {
                        String path_name = key + "/" + filename;
                        logger.info(filename);
                        List<Integer> output = solveInputs(path_name, UnImprovable);
                        List<Task> tasks = parse.read_input_file("Project/inputs/" + path_name);
                        double benefit = parse.check_output(tasks, output);
                        logger.info("Profit: " + Double.toString(benefit));
                        benefits.get(key).add(benefit);
                        }
                    }

                }
            Files.write(Paths.get("UnimprovableInputs.txt"), UnImprovable);
                for (String key:keys) {
                    double total_benefit = 0;
                    for (double benefit:benefits.get(key)) {
                        total_benefit += benefit;
                    }
                    double mean_benefit = total_benefit / benefits.get(key).size();
                    logger.info(key + ": " + Double.toString(mean_benefit));
                }

            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

        }

}


