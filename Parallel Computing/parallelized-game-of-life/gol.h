//
//  gol.h
//  game_of_life_parallelized
//
//  Created by Ting Ting on 10/20/21.
//

#ifndef gol_h
#define gol_h

struct t_arg
{
    int thread_id;            // logical thread id
};

#include <time.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <pthread.h>
#include "./cgif.h"

int dim_w, dim_h;
int nthreads;
pthread_mutex_t mutex;

// grid variables
uint8_t*          pImageData;     // image data (an array of color-indices)
uint8_t*          nextImageData;  // image data (an array of color-indices)

#endif /* gol_h */
