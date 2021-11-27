from parse import read_input_file, write_output_file, read_output_file, check_output
import os
import solver2
import numpy as np

import logging
from datetime import datetime

if __name__ == '__main__':
    logging.basicConfig(filename="GreedySolver2Output_n=4.log", level=logging.INFO)
    logging.info('New Log at ' + datetime.now().strftime('%m/%d/%Y %H:%M:%S'))
    benefits = {'small':[], 'medium':[], 'large':[]}
    if not os.path.isdir('outputs/'):
        os.mkdir('outputs/')
    for x in benefits.keys():
        if not os.path.isdir('outputs/'+x+'/'):
            os.mkdir('outputs/'+x+'/')
        for input_path in os.listdir('inputs/'+x+'/'):
            if input_path[-2:] == 'in':
                path = 'inputs/'+x+'/'+input_path
                tasks = read_input_file(path)
                output = solver2.solve(tasks, 4)
                logging.info('Writing output file for ' + input_path + '...')
                write_output_file('outputs/'+x+'/'+input_path[:-2]+'out', output)
                tasks = read_input_file(path)
                benefits[x].append(check_output(tasks, output))
    for x in benefits.keys():
        benefits[x] = np.mean(benefits[x])
        logging.info(x + ': ' + str(benefits[x]))