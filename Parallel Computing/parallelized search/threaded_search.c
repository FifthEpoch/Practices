#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <stdbool.h>
#include <time.h>

// user input from terminal
long length;        // len. of random number array
long nthreads;      // number of threads
long target_val;    // value to be searched for

// util.
long *rand_array;   // random number array
long *hit_cnt;

// function executed by each thread
void *search_val(void *id) {
    long *myid = (long *) id;
    
    long chunk = length / nthreads;
    long start = *myid * chunk;
    long end  = start + chunk;
    if (*myid == nthreads - 1) {
        end = length;
    }
    
    int i;
    for (i = start; i < end; i++)
    {
        if (rand_array[i] == target_val)
        {
            hit_cnt[*myid]++;
            printf("%d, ", i);
            
        }
    }
    return NULL;
}

int main(int argc, char **argv) {
    
    long *thread_ids;
    pthread_t *thread_array;

    // display help messaging if incorrect number of arguments detected
    if (argc !=4) {
        fprintf(stderr, "usage: %s <n> <p> <s>\n", argv[0]);
        fprintf(stderr, "       where <n> is the number of threads\n");
        fprintf(stderr, "             <p> is the length of array with random integers\n");
        fprintf(stderr, "             <s> is the scalar for multiplying random array\n");
        return 1;
    }
    
    // store user inputs
    nthreads = strtol(argv[1], NULL, 10);
    length = strtol(argv[2], NULL, 10);
    target_val = strtol(argv[3], NULL, 10);
    printf("target value: %d\n", (int)target_val);
    
    
    // allocate space.
    thread_array = malloc(nthreads * sizeof(pthread_t));
    thread_ids = malloc(nthreads * sizeof(long));
    hit_cnt = malloc(nthreads * sizeof(long));
    rand_array = malloc(length * sizeof(long));
    
    // create random number array
    srand((unsigned)time(0));
    int index;
    for (index = 0; index < length; index++) {
        rand_array[index] = rand() % 100;
    }
    
    // init threads
    int i;
    printf("found at indice: ");
    for (i = 0; i < nthreads; i++) {
        thread_ids[i] = i;
        pthread_create(&thread_array[i], NULL, search_val, &thread_ids[i]);
    }

    // join threads
    for (i = 0; i < nthreads; i++) {
        pthread_join(thread_array[i], NULL);
    }
    
    // print results
    int sum = 0;
    for(index = 0; index < nthreads; index++)
    {
        sum += hit_cnt[index];
    }
    printf("\n\nTotal %d found.\n", sum);
    
    // free space
    free(thread_array);
    free(thread_ids);
    free(rand_array);
    free(hit_cnt);

    return 0;
}
