import numpy as np
import os

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
for i in range(50):
    if not os.path.isdir('inputs/100/'):
        os.mkdir('inputs/100/')
    write_input_file('inputs/100/sample' + str(i+1) +'.in', 75, 100)
    if not os.path.isdir('inputs/150/'):
        os.mkdir('inputs/150/')
    write_input_file('inputs/150/sample' + str(i+1) +'.in', 100, 150)
    if not os.path.isdir('inputs/200/'):
        os.mkdir('inputs/200/')
    write_input_file('inputs/200/sample' + str(i+1) +'.in', 150, 200)
