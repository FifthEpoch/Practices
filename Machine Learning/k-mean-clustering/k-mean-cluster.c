
#include "k-mean-cluster.h"

double get_distance(struct point p0, struct point p1)
{
    return (p0.x - p1.x) * (p0.x - p1.x) + (p0.y - p1.y) * (p0.y - p1.y);
}

void iter_clusters(long k, struct cluster *cluster_array)
{
    int i, j;
    
    for (i = 0; i < sizeof point_array; i++)
    {
        struct point cluster_centeroid = {.x = -1, .y = -1, .cluster_id = -1};
        
        int min_cluster = 0;
        double min_dist = 10000.0;
        
        for (j = 0; j < k; j++)
        {
            cluster_centeroid.x = cluster_array[j].sum.x;
            cluster_centeroid.y = cluster_array[j].sum.y;
            
            double dist = get_distance(point_array[i], cluster_centeroid);
            if (dist < min_dist)
            {
                min_dist = dist;
                min_cluster = j;
                
                next_cluster_array[j].sum.x += point_array[i].x;
                next_cluster_array[j].sum.y += point_array[i].y;
                next_cluster_array[j].count ++;
            }
        }
        point_array[i].cluster_id = min_cluster;
        next_cluster_array[min_cluster].sum_dist += min_dist;
    }
}

bool compare_clusters()
{
    for (int i = 0; i < k; i++)
    {
        int centeroid_x = cluster_array[i].sum.x /cluster_array[i].count;
        int centeroid_y = cluster_array[i].sum.y /cluster_array[i].count;
        int next_centeroid_x = next_cluster_array[i].sum.x / next_cluster_array[i].count;
        int next_centeroid_y = next_cluster_array[i].sum.y / next_cluster_array[i].count;
        if ((centeroid_x != next_centeroid_x) || (centeroid_y != next_centeroid_y))
        {
            // copy content in next_cluster_array into cluster_array if they are different
            memcpy(cluster_array, next_cluster_array, k * sizeof(struct cluster));
            printf("current cluster: %d, %d", centeroid_x, centeroid_y);
            printf("next cluster: %d, %d", next_centeroid_x, next_centeroid_y);
            printf("THIS CLUSTER IS DIFFERENT\n");
            return true;
        }
    }
    printf("CLUSTERS ARE THE SAME\n");
    return false;
}

double calculate_mean_dist(long k, struct cluster *cluster_array)
{
    double mean_dist = 0;
    for (int i = 0; i < k; i++)
    {
        mean_dist += cluster_array[i].sum_dist / cluster_array[i].count;
    }
    return mean_dist;
}

void gnuprint(FILE *gp, double x[], double y[])
{
    int i, j;
    char *str  = "plot";
    for (i = 0; i < k; i++)
    {
        str = strcat(str, " '-' with lines,");
    }
    
    for (i = 0; i < k; i++)
    {
        fprintf(gp, "plot '-' with points");
        
        for (j = 0; j < (length / k); j++)
        {
            int index = (int)(i * (length / k) + j);
            fprintf(gp, "%g %g\n", x[index], y[index]);
            fflush(gp);
            fprintf(gp, "e\n");
        }
    }
}

/*error handling function: prints out error message*/
int print_error(char *msg) {
    fprintf(stderr, "%s\n", msg);
    exit(2);
}

int main(int argc, char **argv)
{
    // display help messaging if incorrect number of arguments detected
    if (argc != 4) {
        fprintf(stderr, "usage: %s <k> <l> <n>\n", argv[0]);
        fprintf(stderr, "       <k> is the number of clusters\n");
        fprintf(stderr, "       <l> is the length of array to be sorted\n");
        fprintf(stderr, "       <n> is the number of threads\n");
        return 1;
    }
    
    // store user inputs
    k = strtol(argv[1], NULL, 10);
    printf("k: %li\n", k);
    
    length = strtol(argv[2], NULL, 10);
    printf("length: %li\n", length);
    if (length % k != 0) print_error("ERROR: length must be divisible by k. \n");
    
    nthreads = strtol(argv[3], NULL, 10);
    printf("nthreads: %li\n", nthreads);
    
    point_array = malloc(length * sizeof(struct point));
    if (!point_array) print_error("ERROR: point_array malloc failed. \n");
    printf("point_array address: %s", (char *)&point_array);
    
    cluster_array = malloc(k * sizeof(struct cluster));
    if (!cluster_array) print_error("ERROR: cluster_array malloc failed. \n");
    printf("cluster_array address, %s", (char *)&cluster_array);
    
    next_cluster_array = malloc(k * sizeof(struct cluster));
    if (!next_cluster_array) print_error("ERROR: next_cluster_array malloc failed. \n");
    printf("next_cluster_array address, %s", (char *)&next_cluster_array);
    
    // keep track of the best clusters out of MAX_PASS tries
    best_cluster_array = malloc(k * sizeof(struct cluster));
    if (!best_cluster_array) print_error("ERROR: best_cluster_array malloc failed. \n");
    printf("best_cluster_array address, %s", (char *)&next_cluster_array);
    
    best_point_array = malloc(length * sizeof(struct point));
    if (!best_point_array) print_error("ERROR: best_point_array malloc failed. \n");
    printf("best_point_array address, %s", (char *)&best_point_array);
    
    best_mean = 1000.0;
    
    long i, j;
    
    // generate points
    srand(0);
    for (i = 0; i < length; i++)
    {
        printf("point %li created    ", i);
        struct point temp = {.x = rand() % MAX_VAL, .y = rand() % MAX_VAL, .cluster_id = -1};
        point_array[i] = temp;
    }
    
    // best cluster set out of MAX_PASS tries
    for (i = 0; i < MAX_PASS; i++)
    {
        printf("PASS NO. %li", k);
        // initialize new random starting points
        for (j = 0; j < k; i++)
        {
            struct point p = point_array[rand() % length];
            struct cluster temp = {.count = 0, .sum = {.x = p.x, .y = p.y}};
            cluster_array[j] = temp;
            next_cluster_array[j] = temp;
        }
        // first cluster set using k random points as centeroids
        iter_clusters(k, cluster_array);
        bool clusters_changed = compare_clusters();
        while (clusters_changed)
        {
            iter_clusters(k, cluster_array);
        }
        // calculate mean distance of all points to centeroid
        double mean = calculate_mean_dist(k, next_cluster_array);
        if (mean < best_mean)
        {
            best_mean = mean;
            memcpy(best_cluster_array, next_cluster_array, k * sizeof(struct cluster));
            memcpy(best_point_array, point_array, length * sizeof(struct point));
        }
    }
    // prepare arrays for plotting
    double *point_x = malloc(length * sizeof(double));
    if (!point_x) print_error("ERROR: point_x malloc failed. \n");
    double *point_y = malloc(length * sizeof(double));
    if (!point_y) print_error("ERROR: point_y malloc failed. \n");
    int *counters = malloc(k * sizeof(int));
    if (!counters) print_error("ERROR: counters malloc failed. \n");
    
    for (i = 0; i < length; i++)
    {
        struct point p = point_array[i];
        point_x[p.cluster_id * (length / k) + counters[p.cluster_id]] = p.x;
        point_y[p.cluster_id * (length / k) + counters[p.cluster_id]] = p.y;
        counters[p.cluster_id]++;
    }
    for (i = 0; i < k; i++)
    {
        printf("CLUSTER %ld\n", k);
        for (j = 0; j < length / k; j++)
        {
            printf("(%f, %f), ", point_x[i], point_y[i]);
        }
    }
    
    free(point_array);
    free(cluster_array);
    free(next_cluster_array);
    free(best_cluster_array);
    
    free(point_x);
    free(point_y);
}
