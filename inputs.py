import numpy as np
import os

def write_input_files(path, n1, n2):
    n = np.random.randint(low=n1+1, high=n2, size=1)[0]
    ts = np.random.randint(low=1, high=1440, size=n)
    ds = np.random.randint(low=1, high=60, size=n)
    ps = np.random.normal(50, 1, size=n)**2
    with open(path, 'w') as f:
        f.write(str(n)+'\n')
        for i in range(n):
            f.write(' '.join([str(i), str(ts[i]), str(ds[i]), str(round(ps[i],1))]) + '\n')

if not os.path.isdir('inputs/'):
    os.mkdir('inputs/')
write_input_files('inputs/100.in', 75, 100)
write_input_files('inputs/150.in', 100, 150)
write_input_files('inputs/200.in', 150, 200)


