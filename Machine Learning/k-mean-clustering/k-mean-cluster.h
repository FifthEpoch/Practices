
#ifndef k_mean_cluster_h
#define k_mean_cluster_h

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>
#include <time.h>

#define MAX_VAL 100
#define MAX_PASS 5

struct point {
    double x, y;
    int cluster_id;
};

struct cluster{
    struct point sum;
    int count;
    double sum_dist;
};

long k;
long length;
long nthreads;
struct point *point_array;
struct cluster *cluster_array;
struct cluster *next_cluster_array;
struct cluster *best_cluster_array;
struct point *best_point_array;

double best_mean;

#endif /* k_mean_cluster_h */
