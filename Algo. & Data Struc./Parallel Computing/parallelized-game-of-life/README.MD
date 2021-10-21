# Parallelized Game of Life

Pratice using pthread in C to compute new generations quicker!

![Ants](https://github.com/FifthEpoch/portfolio/blob/master/Algo.%20%26%20Data%20Struc./Parallel%20Computing/parallelized-game-of-life/img/gol.gif)

Compile with 
```
$ gcc -Wall -g -v gol.c cgif.c -o gol_prog 
```

Then, put in parameters 
```
$ ./gol_prog <n> <p> <d> <g>
```

n is number of threads to spin up
p is the number of pixels per side
d is the initial population density in range 1 - 100 (percent)
g is the number of generations to calculate

You will see gol.gif in the directory at the end. 
