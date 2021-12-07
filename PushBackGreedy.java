import java.lang.Math;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;

public class PushBackGreedy {
    public static class ProfitPerMinComparator implements Comparator<Task> {
        public void ProfitPerMinComparator() {
            return;
        }

        @Override
        public int compare(Task t1, Task t2) {
            if (t1.get_max_benefit() / t1.get_duration() > t2.get_max_benefit() / t2.get_duration()) {
                return 1;
            }
            return -1;
        }
    }
    public static class solver {
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

        public void arrange_task(int time, Task task, int index, ArrayList<TimeSlot> total_range) throws Exception {
            TimeSlot ts = total_range.get(index);
            if (time == ts.from && ts.from + task.get_duration() == ts.to) {
                if (index != 0) {
                    total_range.get(index - 1).extend_in_back(time, task);
                    TimeSlot A = TimeSlot.combine_mission_slots(total_range.get(index - 1), total_range.get(index + 1));
                    total_range.remove(index + 1);
                    total_range.remove(index);
                    total_range.remove(index - 1);
                    total_range.add(index - 1, A);
                } else {
                    total_range.get(index + 1).extend_in_front(time, task);
                    total_range.remove(index);
                }
            }
            if (time == ts.from) {
                if (index != 0) {
                    total_range.get(index - 1).extend_in_back(time, task);
                    ts.from += task.get_duration();
                } else {
                    TimeSlot new_task_slot = new TimeSlot(0, task.get_duration(), new ArrayList<Task>(List.of(task)), false);
                    ts.from += task.get_duration();
                    total_range.add(0, new_task_slot);
                }
            } else if (time + task.get_duration() == ts.to) {
                if (ts.to != 1440) {
                    total_range.get(index + 1).extend_in_front(time, task);
                    ts.to -= task.get_duration();
                } else {
                    TimeSlot new_task_slot = new TimeSlot(time, 1440, new ArrayList<Task>(List.of(task)), false);
                    ts.to -= task.get_duration();
                    total_range.add(new_task_slot);
                }
            } else {
                ArrayList<TimeSlot> new_slots = ts.arrange_task_in_middle(time, task);
                total_range.remove(index);
                total_range.add(index, new_slots.get(0));
                total_range.add(index + 1, new_slots.get(1));
                total_range.add(index + 2, new_slots.get(2));
            }
        }

        public List<Integer> search_room(int t_needed, int index, ArrayList<TimeSlot> total_range) {
            int t = t_needed;
            int i = index;
            while (i >= 0) {
                TimeSlot ts = total_range.get(i);
                if (ts.free) {
                    int length = ts.to - ts.from;
                    if (length >= t) {
                        break;
                    } else {
                        t -= length;
                    }
                }
                i -= 1;
            }
            return new ArrayList<>(List.of(i, total_range.get(i).to - t));
        }

        public List<Integer> search_room_backwards(int t_needed, int index, ArrayList<TimeSlot> total_range) {
            int t = t_needed;
            int i = index;
            while (i < total_range.size()) {
                TimeSlot ts = total_range.get(i);
                if (ts.free) {
                    int length = ts.to - ts.from;
                    if (length >= t) {
                        break;
                    } else {
                        t -= length;
                    }
                }
                i += 1;
            }
            return new ArrayList<>(List.of(i, total_range.get(i).from + t));
        }

        public void check_range_slot(ArrayList<TimeSlot> total_range) {
            boolean prev_free = total_range.get(0).free;
            for (int k = 1; k < total_range.size(); k++) {
                if (prev_free && total_range.get(k).free) {
                    total_range.get(k - 1).to = total_range.get(k).to;
                    total_range.remove(k);
                    break;
                } else if ((!prev_free) && (!total_range.get(k).free)) {
                    TimeSlot new_missions = TimeSlot.combine_mission_slots(total_range.get(k - 1), total_range.get(k));
                    total_range.remove(k);
                    total_range.remove(k - 1);
                    total_range.add(k - 1, new_missions);
                }
                prev_free = total_range.get(k).free;
            }
        }

        public int make_room(int start_index, int end_index, int t, ArrayList<TimeSlot> total_range, Task task) {
            TimeSlot new_missions = null;
            int time_end = total_range.get(end_index).to;
            for (int i = start_index; i <= end_index; i ++) {
                if (!total_range.get(i).free) {
                    if (new_missions == null) {
                        new_missions = total_range.get(i);
                    } else {
                        new_missions.go_forward(new_missions.from - total_range.get(i).to);
                        new_missions = TimeSlot.combine_mission_slots(new_missions, total_range.get(i));
                    }
                }
            }
            TimeSlot ts = total_range.get(start_index);
            if (t == ts.from) {
                for (int j = end_index; j >= start_index; j--) {
                    total_range.remove(j);
                }
                TimeSlot longer = new TimeSlot(time_end - task.get_duration(), time_end, new ArrayList<>(), true);
                if (t == 0) {
                    new_missions.go_forward(new_missions.to - longer.from);
                    total_range.add(start_index, new_missions);
                    total_range.add(start_index + 1, longer);
                    return start_index + 1;
                } else {
                    new_missions = TimeSlot.combine_mission_slots(total_range.get(start_index - 1), new_missions);
                    new_missions.go_forward(new_missions.to - longer.from);
                    total_range.remove(start_index - 1);
                    total_range.add(start_index - 1, new_missions);
                    total_range.add(start_index, longer);
                    return start_index;
                }
            } else {
                TimeSlot cut = new TimeSlot(total_range.get(start_index).from, t, new ArrayList<>(), true);
                TimeSlot longer = new TimeSlot(time_end - task.get_duration(), time_end, new ArrayList<>(), true);
                for (int j = end_index; j >= start_index; j--) {
                    total_range.remove(j);
                }
                new_missions.go_forward(new_missions.to - longer.from);
                total_range.add(start_index, cut);
                total_range.add(start_index + 1, new_missions);
                total_range.add(start_index + 2, longer);
                return start_index + 2;
            }
        }

        public int make_room_backwards(int start_index, int end_index, int t, ArrayList<TimeSlot> total_range, Task task) {
            TimeSlot new_missions = null;
            int time_start = total_range.get(start_index).from;
            for (int i = start_index; i <= end_index; i ++) {
                if (!total_range.get(i).free) {
                    if (new_missions == null) {
                        new_missions = total_range.get(i);
                    } else {
                        new_missions.push_back(total_range.get(i).from - new_missions.to);
                        new_missions = TimeSlot.combine_mission_slots(new_missions, total_range.get(i));
                    }
                }
            }
            TimeSlot ts = total_range.get(end_index);
            if (t == ts.to) {
                for (int j = end_index; j >= start_index; j--) {
                    total_range.remove(j);
                }
                TimeSlot longer = new TimeSlot(time_start, time_start + task.get_duration(), new ArrayList<>(), true);
                if (t == 1440) {
                    new_missions.push_back(longer.to - new_missions.from);
                    total_range.add(start_index, longer);
                    total_range.add(start_index + 1, new_missions);
                    return start_index;
                } else {
                    new_missions = TimeSlot.combine_mission_slots(new_missions, total_range.get(start_index + 1));
                    new_missions.push_back(longer.to - new_missions.from);
                    total_range.remove(start_index);
                    total_range.add(start_index, longer);
                    total_range.add(start_index + 1, new_missions);
                    return start_index;
                }
            } else {
                TimeSlot cut = new TimeSlot(t, total_range.get(end_index).to, new ArrayList<>(), true);
                TimeSlot longer = new TimeSlot(time_start, time_start + task.get_duration(), new ArrayList<>(), true);
                for (int j = end_index; j >= start_index; j--) {
                    total_range.remove(j);
                }
                new_missions.push_back(longer.to - new_missions.from);
                total_range.add(start_index, longer);
                total_range.add(start_index + 1, new_missions);
                total_range.add(start_index + 2, cut);
                return start_index;
            }
        }


        public List<Integer> solve(List<Task> tasks, int Max_time, int time_interval) throws Exception {
            tasks.sort(new ProfitPerMinComparator());

            ArrayList<TimeSlot> total_range = new ArrayList<>();
            total_range.add(new TimeSlot(0, 1440, new ArrayList<>(), true));
            int time_left = Max_time;

            for (Task task: tasks) {
                if (task.get_duration() > time_left) {
                    continue;
                }
                int index = 0;
                int ddl = task.get_deadline();
                int best_starting_time = ddl - task.get_duration();
                int forward_time_available = 0;
                for (TimeSlot ts: total_range) {
                    if (ts.to <= best_starting_time) {
                        if (ts.free) {
                            forward_time_available += ts.to - ts.from;
                        }
                    } else {
                        if (ts.free) {
                            if (ts.arrangable(best_starting_time, task)) {
                                arrange_task(best_starting_time, task, index, total_range);
                                break;
                            } else {
                                if (ts.arrangable(ts.from, task)) {
                                    arrange_task(ts.from, task, index, total_range);
                                    break;
                                } else {
                                    int time_needed = task.get_duration();
                                    if (time_needed <= forward_time_available) {
                                        List<Integer> results = search_room(time_needed, index, total_range);
                                        int new_index = make_room(results.get(0), index, results.get(1), total_range, task);
                                        check_range_slot(total_range);
                                        arrange_task(total_range.get(new_index).from, task, new_index, total_range);
                                        break;
                                    } else {
                                        int new_index = index;
                                        if (forward_time_available > 0) {
                                            List<Integer> results = search_room(forward_time_available, index, total_range);
                                            new_index = make_room(results.get(0), index, results.get(1), total_range, task);
                                        }
                                        time_needed -= forward_time_available;
                                        List<Integer> results2 = search_room_backwards(time_needed, new_index, total_range);
                                        int new_index2 = make_room_backwards(new_index, results2.get(0), results2.get(1), total_range, task);
                                        check_range_slot(total_range);
                                        int final_index = new_index2 - 1;
                                        arrange_task(total_range.get(new_index2).from, task, new_index2, total_range);
                                        break;
                                    }
                                }
                            }
                        } else {
                            int time_needed = task.get_duration();
                            if (time_needed <= forward_time_available) {
                                List<Integer> results = search_room(time_needed, index, total_range);
                                int new_index = make_room(results.get(0), index, results.get(1), total_range, task);
                                check_range_slot(total_range);
                                arrange_task(total_range.get(new_index).from, task, new_index, total_range);
                                break;
                            } else {
                                int new_index = index;
                                if (forward_time_available > 0) {
                                    List<Integer> results = search_room(forward_time_available, index, total_range);
                                    new_index = make_room(results.get(0), index, results.get(1), total_range, task);
                                }
                                time_needed -= forward_time_available;
                                List<Integer> results2 = search_room_backwards(time_needed, new_index, total_range);
                                int new_index2 = make_room_backwards(new_index, results2.get(0), results2.get(1), total_range, task);
                                int final_index = new_index2 - 1;
                                check_range_slot(total_range);
                                arrange_task(total_range.get(new_index2).from, task, new_index2, total_range);
                                break;
                            }
                        }
                    }
                    index += 1;
                }

                time_left -= task.get_duration();
            }

            ArrayList<Integer> output = new ArrayList<>();
            ArrayList<Task> tsks = new ArrayList<>();
            double profit = 0.0;
            for (TimeSlot ts: total_range) {
                tsks.addAll(ts.tasks);
                profit += ts.getProfit();
            }

            for (Task task: tsks) {
                output.add(task.get_task_id());
            }

            System.out.println(profit);
            System.out.println(output);

            return output;
        }
    }

    public static void main(String[] args) throws Exception{
        PushBackGreedy greedy = new PushBackGreedy();
        parse p = new parse();
        PushBackGreedy.solver s = new solver();
        List<Task> tasks = p.read_input_file("samples/100.in");
        List<Integer> output = s.solve(tasks, 1440, 60);
        System.out.println(output);
    }
}