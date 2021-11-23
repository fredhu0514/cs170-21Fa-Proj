import solver
import logging
from parse import read_input_file, write_output_file

log = "test.log"
logging.basicConfig(filename=log, level=logging.DEBUG, format='%(asctime)s %(message)s', datefmt='%d/%m/%Y %H:%M:%S')
logging.info("Ready to go:  ")

for data_num in [100, 150, 200]:
    benefits_list = []
    for i in range(1, 51):
        tasks = read_input_file(f'./test/{data_num}/sample{i}.in')
        output = solver.solve(tasks)
        benefits = solver.get_total_benefit(read_input_file(f'./test/{data_num}/sample{i}.in'), output)
        benefits_list.append(benefits)
        write_output_file(f'./output/{data_num}/sample{i}.out', output)
    logging.info(f"Length {data_num} avg50 benefits = {sum(benefits_list)/len(benefits_list)}")


