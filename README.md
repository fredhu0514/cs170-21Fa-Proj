# Data in this branch serves for te purpose of validation.
### For each length of input (100, 150, 200), 50 sample inputs named as `sample1.in`  to `sample50.in`.

### You should put `inputs` in the same folder as `solver.py`. Then generate the result to an `outputs` folder.

```angular2html
.
|--- _outputs
|    |--- _100
|    |    |
|    |--- _150
|    |--- _200

```
```
.
├── ouptuts
│   ├── 100
│   │   ├── sample1.out
│   │   ├── [...]
│   │   └── sample50.out
│   ├── 150
│   │   ├── sample1.out
│   │   ├── [...]
│   │   └── sample50.out
│   └── 200
│       ├── sample1.out
│       ├── [...]
│       └── sample50.out
├── inputs
│   ├── 100
│   │   ├── sample1.in
│   │   ├── [...]
│   │   └── sample50.in
│   ├── 150
│   │   ├── sample1.in
│   │   ├── [...]
│   │   └── sample50.in
│   └── 200
│       ├── sample1.in
│       ├── [...]
│       └── sample50.in
├── solver.py
├── parse.py
├── Task.py
├── README.md
├── test.py
└── test.log
```