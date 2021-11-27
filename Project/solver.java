package Project;
import Project.Task;
import Project.parse;

import java.lang.reflect.Array;
import java.util.*;
import java.lang.Math;

public class solver {

    public static List<Integer> solve(List<Task> tasks) {
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
        for (List<Integer> comb: allCombinations) {
            if (comb.size() == 2) {
                // [id, id], only one task is chosen and completed
                Task task = tasks.get(1);
                List<Double> currKeyValue = new ArrayList<>();
                currKeyValue.add((double) task.get_duration());
                currKeyValue.add(task.get_late_benefit(task.get_duration()-task.get_deadline()));
                currKeyValue.add((double) task.get_task_id());
                map.put(comb, currKeyValue);
                continue;
            }
            int i = comb.get(comb.size()-1);
            Task task = tasks.get(i);
            List<Integer> prevPath = new ArrayList<>(); // get S - {i}
            for (int j = 0; j < comb.size()-1; j++) {
                if (comb.get(j) != i) {
                    prevPath.add(j);
                }
            }
            List<List<Integer>> PossiblePrevCombs = new ArrayList<>(); // get all possible [S - {i} - {j}, j]
            for (int id:prevPath) {
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
                for (List<Integer> prevComb: PossiblePrevCombs) {
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
        for (double id: OptSeq) {
            task_ids.add((int) id);
        }
        return task_ids;

    }

    public static List<int[]> PowerList(int n) {
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
            for (int[] x: temp) {
                for (int j = x[i-2]+1; j <= n; j++) { // j must be larger than the last element of x
                    boolean contains = false;
                    for (int k = 0; k < i-1; k++) { //x.length = i-1
                        if (x[k] == j) {
                            contains = true;
                            break;
                        }
                    }
                    if (!contains) {
                        int[] newX = new int[i];
                        for (int k = 0; k < i-1; k++) {
                            newX[k] = x[k];
                        }
                        newX[i-1] = j;
                        newTemp.add(newX);
                    }
                }
            }
            temp = newTemp;
            list.addAll(temp);
        }
        return list;
    }

    public static List<List<Integer>> AllCombinations(int n) {
        /*
        *  returns [S, id] for all S in PowerList and id in S
        * */
        List<List<Integer>> list = new ArrayList<>();
        List<int[]> powerlist = PowerList(n);
        for (int[] lst:powerlist) {
            for (int x:lst) {
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

    public static void main(String[] args) throws Exception{
        List<Task> tasks = parse.read_input_file("Project/inputs/small/small-1.in");
        List<Integer> output = solve(tasks);
        System.out.println(output);
    }
}

