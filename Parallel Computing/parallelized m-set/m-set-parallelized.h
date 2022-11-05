
#ifndef m_set_parallelized_h
#define m_set_parallelized_h

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <time.h>

#include "cgif.h"

#define MAGN        1.0
#define ITER_MAX    100

int h, w;
int nthreads;
float upper, lower;

struct t_arg
{
    int thread_id;            // logical thread id
};

// grid variables
uint8_t*          pImageData;     // image data (an array of color-indices)

#endif /* m_set_parallelized_h */
