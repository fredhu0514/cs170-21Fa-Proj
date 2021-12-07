# CS 170 Project Fall 2021

- Use [current time, task duration, task ddl, task profit at current time, if we choose this task at this moment] as the training data set and get an SVM model for further predictions.

- At each round if no value is predicted to be 1, then we do deterministic greedy as value/timestamp. Else, we select the largest value/timestamp from the output given 1s.

- Only get 91 small cases, the result is awful. Possibly because our training data is not the optimal (our current best solution is suboptimal). Furthermore, the training is not perfect, must lost some dimension of information.

- Therefore, do not recommend this method.
