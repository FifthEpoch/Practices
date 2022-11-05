
#ifndef k_mean_cluster_h
#define k_mean_cluster_h

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>
#include "omp.h"

#define MAX_VAL 100
#define GNU_PATH "/usr/local/bin/gnuplot"

struct point {
    int x, y;
    int cluster_id;
    double dist_from_centroid;
};

struct cluster{
    struct point centroid;
    int count;
    double sum_dist;
};

long k;
long length;
long pass;
struct point *point_array;
struct cluster *cluster_array;
struct cluster *next_cluster_array;
struct cluster *best_cluster_array;
struct point *best_point_array;
double *mean_std_array;

double best_mean;

#endif /* k_mean_cluster_h */
