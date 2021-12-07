import solver
import logging
from parse import read_input_file, write_output_file

log = "test.log"
logging.basicConfig(filename=log, level=logging.DEBUG, format='%(asctime)s %(message)s', datefmt='%d/%m/%Y %H:%M:%S')
logging.info("Ready to go:  ")

for data_num in ["small", "medium", "large"]:
    benefits_list = []
    for i in range(1, 301):
        tasks = read_input_file(f'./inputs/{data_num}/{data_num}-{i}.in')
        output = solver.solve(tasks)
        print(f'./input/{data_num}/{data_num}-{i}.in')
        benefits = solver.get_total_benefit(read_input_file(f'./inputs/{data_num}/{data_num}-{i}.in'), output)
        benefits_list.append(benefits)
        write_output_file(f'./outputs/{data_num}/{data_num}-{i}.out', output)
    logging.info(f"Length {data_num} avg300 benefits = {sum(benefits_list)/len(benefits_list)}")
