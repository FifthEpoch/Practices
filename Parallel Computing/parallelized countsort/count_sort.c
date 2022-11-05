
#include "count_sort.h"
#define MAX 10

/*error handling function: prints out error message*/
int print_error(char *msg) {
    fprintf(stderr, "%s\n", msg);
    exit(2);
}

/* helper function: genRandomArray
 * fills an input array of specified length (length) with random
   values from 0 to MAX-1
*/
void genRandomArray(int *array, long length) {
    int i;
    for (i = 0; i < length; i++) {
        array[i] = rand() % MAX;
    }
}

/* helper function: printArray
 * prints out all the values in the input array separated by spaces
 */
void printArray(int *array, long length) {
    int i;
    for (i = 0; i < length; i++) {
        printf("%d ", array[i]);
    }
    printf("\n");
}

/*helper function: printCounts
 * prints out all the values in the counts array, separated by spaces
*/
void printCounts(long *counts) {
    int i;
    for (i = 0; i < MAX; i++) {
        printf("%ld ", counts[i]);
    }
    printf("\n");
}

void printRes(float p, float s, int core_num, int len)
{
    float speedup = s / p;
    float eff = speedup / core_num;
    printf("\nSTATS for countsort with array of length %d: \n\n", len);
    printf(">> serial:                      %d micro seconds\n", (int)s);
    printf(">> parallel:                    %d micro seconds (with %d cores)\n\n", (int)p, core_num);
    printf(">> speedup:                     %.5f\n", speedup);
    printf(">> efficiency:                  %.5f\n\n", eff);
}

void printRes(float duration,int core_num)
{
    printf(">> nested parallel:                    %d micro seconds (with %d cores)\n\n", (int)duration, core_num);
}


// PARALLEL -----------------------------------------------------------

struct t_arg {
    long id; //thread id
    long numthreads; //number of threads
    int *ap; //pointer to array to be sorted
    int length; //length of  array to be sorted
    long *countp; //pointer to count array
};

pthread_mutex_t mutex;

/*parallel version of step 1 (first cut) of CountSort algorithm:
 * extracts arguments from args value
 * calculates portion of the array this thread is responsible for counting
 * computes the frequency of all the elements in assigned component and stores
 * the associated counts of each element in counts array
*/
void *countElems(void *args) {
    //extract arguments
    struct t_arg *myargs = (struct t_arg *)args;
    long myid = myargs->id;
    long nthreads = myargs->numthreads;
    long length = myargs->length;
    int *array = myargs->ap;
    long *counts = myargs->countp;

    //assign work to the thread
    long chunk = length / nthreads; //nominal chunk size
    long start = myid * chunk;
    long end = (myid + 1) * chunk;
    long val;
    if (myid == nthreads - 1) {
        end = length;
    }
    
    pthread_mutex_lock(&mutex);
    long i;
    //heart of the program
    for (i = start; i < end; i++) {
        val = array[i];
        counts[val] = counts[val] + 1;
    }
    pthread_mutex_unlock(&mutex);

    return NULL;
}

// SERIAL -------------------------------------------------------------

/*step 1:
 * compute the frequency of all the elements in the input array and store
 * the associated counts of each element in array counts. The elements in the
 * counts array are initialized to zero prior to the call to this function.
*/
void countElems_s(int *counts, int *array_A, long length) {
    int val, i;
    for (i = 0; i < length; i++) {
        val = array_A[i]; //read the value at index i
        counts[val] = counts[val] + 1; //update corresponding location in counts
    }
}

/* step 2:
 * overwrite the input array (array_A) using the frequencies stored in the
 *  array counts
*/
void writeArray(int *counts, int *array_A) {
    int i, j = 0, amt;

    for (i = 0; i < MAX; i++) { //iterate over the counts array
        amt = counts[i]; //capture frequency of element i
        while (amt > 0) { //while all values aren't written
            array_A[j] = i; //replace value at index j of array_A with i
            j++; //go to next position in array_A
            amt--; //decrease the amount written by 1
        }
    }
}

int main(int argc, char **argv) {

    if (argc != 4) {
        fprintf(stderr, "usage: %s <n> <p?> <t>\n", argv[0]);
        fprintf(stderr, "where <n> is the length of the array\n");
        fprintf(stderr, "and <p?> is the print option (0/1)\n");
        fprintf(stderr, "and <t> is the number of threads\n");
        return 1;
    }

    srand(10);

    long t;
    long length = strtol(argv[1], NULL, 10);
    int verbose = atoi(argv[2]);
    long nthreads = strtol(argv[3], NULL, 10);
    if (nthreads < 1) print_error("ERROR: need a positive number of threads");
    if (length < nthreads) print_error("ERROR: length must be greater than nthreads");
    int ret; //for error checking

    //generate random array of elements of specified length
    int *array = malloc(length * sizeof(int));
    if (!array) print_error("ERROR: malloc failed");

    genRandomArray(array, length);
    
    if (verbose)
    {
        printf("array before sort:\n");
        printArray(array, length);
    }
    
    // time value struct from time.h
    struct timeval p_start, p_end, s_start, s_end;
    
    // start time for parallel program
    gettimeofday(&p_start, NULL);
    
    //specify counts array
    long counts[MAX] = {0};
    
    //allocate threads and args array
    pthread_t *thread_array; //pointer to future thread array
    thread_array = malloc(nthreads * sizeof(pthread_t)); //allocate the array
    struct t_arg *thread_args = malloc(nthreads * sizeof(struct t_arg));
    if (!thread_array || !thread_args) print_error("ERROR: malloc failed");

  //fill thread array with parameters
    for (t = 0; t < nthreads; t++) {
        thread_args[t].id = t;
        thread_args[t].numthreads = nthreads;
        thread_args[t].ap = array; //pointer to array
        thread_args[t].length = length;
        thread_args[t].countp = counts; //pointer to counts array
    }
    ret = pthread_mutex_init(&mutex, NULL); //initialize the mutex
        if (ret) print_error("ERROR: pthread_mutex_init failed");
    
    for (t = 0; t < nthreads; t++) {
        ret = pthread_create( &thread_array[t], NULL, countElems, &thread_args[t]);
        if (ret) print_error("ERROR: pthread_create failed");
    }

    for (t = 0; t < nthreads; t++) {
        ret = pthread_join(thread_array[t], NULL);
        if (ret) print_error("ERROR: pthread_create failed");
    }
    
    pthread_mutex_destroy(&mutex);
    free(thread_array);
    free(array);
    
    // end time for parallel program
    gettimeofday(&p_end, NULL);
    
    // start time for serial program
    gettimeofday(&s_start, NULL);
    
    int s_counts[MAX] = {0};
    countElems_s(s_counts, array, length); //calls step 1
    writeArray(s_counts, array); //calls step2

    // start time for serial program
    gettimeofday(&s_end, NULL);
    
    if (verbose) {
        printf("Counts array:\n");
        printCounts(counts);
    }
    
    
    
    float p_duration = ((p_end.tv_sec * 1000000 + p_end.tv_usec) -
                        (p_start.tv_sec * 1000000 + p_start.tv_usec));
    float s_duration = ((s_end.tv_sec * 1000000 + s_end.tv_usec) -
                       (s_start.tv_sec * 1000000 + s_start.tv_usec));
    
    printRes(p_duration, s_duration, nthreads, length);
    
    return 0;
}

