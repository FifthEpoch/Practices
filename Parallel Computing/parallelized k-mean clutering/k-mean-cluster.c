
#include "k-mean-cluster.h"

double get_distance(struct point p0, struct point p1)
{
    return ((p0.x - p1.x) * (p0.x - p1.x)) + ((p0.y - p1.y) * (p0.y - p1.y));
}

void iter_clusters(int thread_id, struct point *_point_array, struct cluster *_cluster_array, struct cluster *_next_cluster_array)
{
    // clear _next_cluster_array data
#pragma omp parallel for
    for (int n = 0; n < k; n++)
    {
        _next_cluster_array[n].count = 0;
        _next_cluster_array[n].centroid.x = 0;
        _next_cluster_array[n].centroid.y = 0;
        _next_cluster_array[n].sum_dist = 0;
    }
    // find the centroid closest to points
    for (int n = 0; n < length; n++)
    {
        int min_cluster = -1;
        double min_dist = 9007199254740990;
        double dist = -1;
        
        for (int j = 0; j < k; j++)
        {
            struct point cluster_centeroid =
            {
                .x = (_cluster_array[j].centroid.x / _cluster_array[j].count),
                .y = (_cluster_array[j].centroid.y / _cluster_array[j].count),
                .cluster_id = j
                
            };
            dist = get_distance(_point_array[n], cluster_centeroid);
#pragma omp critical
            {
                if (dist < min_dist)
                {
                    min_dist = dist;
                    min_cluster = j;
                }
            }
        }
        // update _next_cluster_array data
#pragma omp critical
        {
            _next_cluster_array[min_cluster].centroid.x += _point_array[n].x;
            _next_cluster_array[min_cluster].centroid.y += _point_array[n].y;
            _next_cluster_array[min_cluster].sum_dist += min_dist;
            _next_cluster_array[min_cluster].count++;
            
            _point_array[n].cluster_id = min_cluster;
            _point_array[n].dist_from_centroid = dist;
        }
    }
}

bool compare_clusters(int thread_id, struct cluster *_cluster_array, struct cluster *_next_cluster_array)
{
    bool is_different = false;
#pragma omp parallel for num_threads(k)
    for (int n = 0; n < k; n++)
    {
        int centeroid_x = _cluster_array[n].centroid.x / _cluster_array[n].count;
        int centeroid_y = _cluster_array[n].centroid.y / _cluster_array[n].count;
        
        int next_centeroid_x = _next_cluster_array[n].centroid.x / _next_cluster_array[n].count;
        int next_centeroid_y = _next_cluster_array[n].centroid.y / _next_cluster_array[n].count;
        
        if ((centeroid_x != next_centeroid_x) || (centeroid_y != next_centeroid_y))
        {
#pragma omp critical
            is_different = true;
        }
    }
    if (is_different)
    {
        // copy content in next_cluster_array into cluster_array if they are different
        memcpy(_cluster_array, _next_cluster_array, k * sizeof(struct cluster));
        return true;
    } else
    {
        return false;
    }
}

double calculate_cluster_avg_std(long k, struct cluster *_cluster_array, struct point *_point_array)
{
    double *mean_per_cluster = malloc(k * sizeof(double));
    double *std_per_cluster = malloc(k * sizeof(double));
    double avg_std = 0.0;
    
#pragma omp parallel for
    for (int n = 0; n < k; n++)
    {
        mean_per_cluster[n] = _cluster_array[n].sum_dist / _cluster_array[n].count;
    }
#pragma omp parallel for
    for (int n = 0; n < length; n++)
    {
        int id = _point_array[n].cluster_id;
        std_per_cluster[id] += (_point_array[n].dist_from_centroid - mean_per_cluster[id]);
    }
#pragma omp parallel for reduction(+:avg_std)
    for (int n = 0; n < k; n++)
    {
        avg_std += std_per_cluster[n];
    }
    
    free(mean_per_cluster);
    free(std_per_cluster);
    
    return (avg_std / (double)k);
}

/*error handling function: prints out error message*/
int print_error(char *msg) {
    fprintf(stderr, "%s\n", msg);
    exit(2);
}

void write_n_plot(int *counters, int* x, int * y)
{
    char commands[k][50];
    
    // writing cluster data into different files for plotting in different colors
    for (int n = 0; n < k; n++)
    {
        char str[50];
        snprintf(str, 20, "cluster_%i.dat", n);
        snprintf(commands[n], 50, " '%s' with points pointsize 1", str);
        FILE *fPtr = fopen(str, "w");
        
        if(fPtr == NULL) print_error("File creation failed!");
        
        for (int j = n * (int)length; j < n * (int)length + counters[n]; j++)
        {
            printf("%i, %i\n", x[j], y[j]);
            fprintf(fPtr, "%i\t%i\n", x[j], y[j]);
        }
        fclose(fPtr);
    }
    
    // construct command
    char command[500] = "plot [0:100]";
    for (int i = 0; i < k; i++)
    {
        strcat(command, commands[i]);
        strcat(command, ",");
    }
    command[strlen(command) - 1] = '\0';
    char gnu_path[100] = "";
    strcat(gnu_path, GNU_PATH);
    strcat(gnu_path, " -persistent");
    
    // plot with the constructed command
    FILE *gnuplotPipe = popen(gnu_path, "w");
    fprintf(gnuplotPipe, "%s \n", command);
}

int main(int argc, char **argv)
{
    // display help messaging if incorrect number of arguments detected
    if (argc != 4) {
        fprintf(stderr, "usage: %s <k> <l> <n>\n", argv[0]);
        fprintf(stderr, "       <k> is the number of clusters\n");
        fprintf(stderr, "       <l> is the length of array to be sorted\n");
        fprintf(stderr, "       <n> is the number of k-mean passes\n");
        return 1;
    }
    
    // store user inputs
    k = strtol(argv[1], NULL, 10);
    printf("k: %li\n", k);
    
    length = strtol(argv[2], NULL, 10);
    printf("length: %li\n", length);
    if (length < k) print_error("ERROR: length must be larger than number of clusters. \n");
    
    pass = strtol(argv[3], NULL, 10);
    printf("pass: %li\n", pass);
    
    point_array = malloc(length * sizeof(struct point));
    if (!point_array) print_error("ERROR: point_array malloc failed. \n");
    printf("point_array address: %pn\n", &point_array);
    
    // keep track of the best clusters out of MAX_PASS tries
    best_cluster_array = malloc(k * sizeof(struct cluster));
    if (!best_cluster_array) print_error("ERROR: best_cluster_array malloc failed. \n");
    printf("best_cluster_array address: %pn\n", &next_cluster_array);
    
    best_point_array = malloc(length * sizeof(struct point));
    if (!best_point_array) print_error("ERROR: best_point_array malloc failed. \n");
    printf("best_point_array address: %pn\n", &best_point_array);
    
    mean_std_array = malloc(pass * sizeof(double));
    if (!mean_std_array) print_error("ERROR: mean_std_array malloc failed. \n");
    
    best_mean = 9007199254740990;
    
    // generate points
    srand(0);
#pragma omp parallel for num_threads(length)
    for (int i = 0; i < length; i++)
    {
        struct point temp = {.x = rand() % MAX_VAL, .y = rand() % MAX_VAL, .cluster_id = -1, .dist_from_centroid = -1};
        point_array[i] = temp;
    }
    // best cluster set out of MAX_PASS tries
    omp_set_max_active_levels(2147483645);
#pragma omp parallel for num_threads(pass)
    for (int i = 0; i < pass; i++)
    {
        struct point *i_point_array;
        i_point_array = malloc(length * sizeof(struct point));
        if (!i_point_array) print_error("ERROR: i_point_array malloc failed. \n");
        memcpy(i_point_array, point_array, length * sizeof(struct point));
        printf("thread %i: i_point_array address: %pn\n", i, &i_point_array);
        
        struct cluster *i_cluster_array, *i_next_cluster_array;
        i_cluster_array = malloc(k * sizeof(struct cluster));
        if (!i_cluster_array) print_error("ERROR: i_cluster_array malloc failed. \n");
        printf("thread %i: i_cluster_array address, %pn\n", i, &i_cluster_array);
        
        i_next_cluster_array = malloc(k * sizeof(struct cluster));
        if (!i_next_cluster_array) print_error("ERROR: i_next_cluster_array malloc failed. \n");
        printf("thread %i: i_next_cluster_array address: %pn\n", i, &i_next_cluster_array);
        
        // initialize new random starting points
#pragma omp parallel for
        for (int j = 0; j < k; j++)
        {
            struct point p = i_point_array[((int)(((double)length /(double) k) * j) + i) % length];
            printf("thread %i: (%i, %i)\n", i, p.x, p.y);
            
            struct cluster temp = {.count = 1, .sum_dist = 0.0, .centroid = {.x = p.x, .y = p.y, .cluster_id = j}};
            
            memcpy(&i_next_cluster_array[j], &temp, sizeof(struct cluster));
            memcpy(&i_cluster_array[j], &temp, sizeof(struct cluster));
        }
        
        // first cluster set using k random points as centeroids
        iter_clusters(i, i_point_array, i_cluster_array, i_next_cluster_array);
        int t = 0;
        while (compare_clusters(i, i_cluster_array, i_next_cluster_array))
        {
            printf("thread %i's t: %i\n", i, t);

            iter_clusters(i, i_point_array, i_cluster_array, i_next_cluster_array);
            t++;
        }
        // calculate mean distance of all points to centeroid
        mean_std_array[i] = calculate_cluster_avg_std(k, i_next_cluster_array, i_point_array);
        printf("mean std for thread %i:  %f\n", i, mean_std_array[i]);
        
        // copy points and cluster if mean < best_mean
#pragma omp critical
        {
            if (mean_std_array[i] < best_mean)
            {
                best_mean = mean_std_array[i];
                memcpy(best_cluster_array, i_next_cluster_array, k * sizeof(struct cluster));
                memcpy(best_point_array, i_point_array, length * sizeof(struct point));
            }
        }
        free(i_point_array);
        free(i_cluster_array);
        free(i_next_cluster_array);
    }
    
    // prepare arrays for plotting
    int *point_x = malloc(k * length * sizeof(double));
    if (!point_x) print_error("ERROR: point_x malloc failed. \n");
    int *point_y = malloc(k * length * sizeof(double));
    if (!point_y) print_error("ERROR: point_y malloc failed. \n");
    
    int *counters = malloc(k * sizeof(int));
    if (!counters) print_error("ERROR: counters malloc failed. \n");
    memset(counters, 0, k * sizeof(int));

#pragma omp parallel for
    for (int i = 0; i < length; i++)
    {
        struct point p = best_point_array[i];
        printf("p's cluster id: %i\n", p.cluster_id);
        point_x[(p.cluster_id * length) + counters[p.cluster_id]] = p.x;
        point_y[(p.cluster_id * length) + counters[p.cluster_id]] = p.y;
        counters[p.cluster_id]++;
    }
    
    for (int i = 0; i < k; i++)
    {
        printf("CLUSTER %i\n", i);
#pragma omp parallel for
        for (int j = i * (int)length; j < (i * (int)length) + counters[i]; j++)
        {
            printf("(%i, %i), ", point_x[j], point_y[j]);
        }
        printf("\n");
    }
    
    write_n_plot(counters, point_x, point_y);
    
    free(point_array);
    free(cluster_array);
    
    free(best_point_array);
    free(best_cluster_array);
    free(mean_std_array);
    
    free(point_x);
    free(point_y);
    free(counters);
}

