import numpy as np
import os
from parse import read_input_file

def write_input_file(path, n1, n2):
    n = np.random.randint(low=n1+1, high=n2, size=1)[0]
    ts = np.random.randint(low=1, high=1440, size=n)
    ds = np.random.randint(low=1, high=60, size=n)
    ps = np.random.rand(n)*99 + 0.5
    with open(path, 'w') as f:
        f.write(str(n)+'\n')
        for i in range(n):
            f.write(' '.join([str(i+1), str(ts[i]), str(ds[i]), str(round(ps[i],1))]) + '\n')

if not os.path.isdir('inputs/'):
    os.mkdir('inputs/')
write_input_file('inputs/100.in', 75, 100)
write_input_file('inputs/150.in', 100, 150)
write_input_file('inputs/200.in', 150, 200)

read_input_file('inputs/100.in')
read_input_file('inputs/150.in')
read_input_file('inputs/200.in')
