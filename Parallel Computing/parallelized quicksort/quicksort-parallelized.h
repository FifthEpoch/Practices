
#ifndef nested_pthreads_h
#define nested_pthreads_h

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

pthread_mutex_t mutex;

struct t_arg {
    long hi;
    long lo;
};

// user input from terminal
long length;        // len. of random number array

// util.
long *rand_array;   // random number array

#endif /* nested_pthreads_h */
