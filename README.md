# Data in this branch serves for te purpose of validation.
### For each length of input (small, medium, large), 300 sample inputs named such as `small_1.in`  to `small_300.in`.

### You should put `inputs` in the same folder as `solver.py`. Then generate the result to an `outputs` folder.

```angular2html
.
|--- _outputs
|    |--- _small
|    |    |
|    |--- _medium
|    |--- _large

```
```
.
├── outputs
│   ├── small
│   │   ├── small_1.out
│   │   ├── [...]
│   │   └── small_300.out
│   ├── medium
│   │   ├── medium_1.out
│   │   ├── [...]
│   │   └── medium_300.out
│   └── large
│       ├── large_1.out
│       ├── [...]
│       └── large_300.out
├── inputs
│   ├── small
│   │   ├── small_1.in
│   │   ├── [...]
│   │   └── small_300.in
│   ├── medium
│   │   ├── medium_1.in
│   │   ├── [...]
│   │   └── medium_300.in
│   └── large
│       ├── large_1.in
│       ├── [...]
│       └── large_300.in
├── solver.py
├── parse.py
├── Task.py
├── README.md
├── test.py
└── test.log
```