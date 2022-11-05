//
//  scalar-multiplier.c
//  pp_pthread_lab01
//
//  Created by Ting Ting on 10/7/21.
//

#include "scalar-multiplier.h"
#define MAX 10

/*error handling function: prints out error message*/
int print_error(char *msg)
{
    fprintf(stderr, "%s\n", msg);
    exit(2);
}

struct t_arg
{
    int *array; // pointer to shared array
    long length; // num elements in array
    long s; //scaling factor
    long numthreads; // total number of threads
    long id; //  logical thread id
};

pthread_mutex_t mutex;

void * scalar_multiply(void* args)
{
    //cast to a struct t_arg from void*
    struct t_arg * myargs = (struct t_arg *) args;

    //extract all variables from struct
    long myid =  myargs->id;
    long length = myargs->length;
    long s = myargs->s;
    long nthreads = myargs->numthreads;
    int * ap = myargs->array; //pointer to array in main

    int chunk = length / nthreads;
    int remainder = length % nthreads;
    int start, end;
    
    if (remainder > myid)
    {
        start = (chunk + 1) * myid;
        end = start + (chunk + 1);
    } else {
        start = ((chunk + 1) * remainder) + (chunk * (myid - remainder));
        end = start + chunk;
    }
    
    pthread_mutex_lock(&mutex);
    
    int i;
    for (i = start; i < end; i++)
    {
        ap[i] *= s;
    }
    
    pthread_mutex_unlock(&mutex);
    
    return NULL;
}

void s_scalar_multiple(int* array, long length, long s)
{
    int i;
    for (i = 0; i < length; i++)
    {
        array[i] *= s;
    }
}

int main(int argc, char **argv)
{
    
    // error checking
    
    int ret;

    
    // display help messaging if incorrect number of arguments detected
    
    if (argc !=4)
    {
        fprintf(stderr, "usage: %s <n> <p> <s>\n", argv[0]);
        fprintf(stderr, "       where <n> is the number of threads\n");
        fprintf(stderr, "             <p> is the length of array with random integers\n");
        fprintf(stderr, "             <s> is the scalar for multiplying random array\n");
        return 1;
    }
    
    
    // store user inputs
    
    long nthreads = strtol(argv[1], NULL, 10);
    if (nthreads < 1) print_error("ERROR: need a positive number of threads");
    
    long length = strtol(argv[2], NULL, 10);
    if (length < nthreads) print_error("ERROR: length must be greater than nthreads");
    
    long s = strtol(argv[3], NULL, 10);
    
    printf("\n>> multiply array of size %ld with scalar %ld with %ld cores\n", length, s, nthreads);
    
    
    // allocate space
    
    pthread_t* thread_array = malloc(nthreads * sizeof(pthread_t));
    if (!thread_array) print_error("ERROR: malloc failed at thread_array");
    
    int* rand_array = malloc(length * sizeof(int));
    if (!rand_array) print_error("ERROR: malloc failed at rand_array");
    
    struct t_arg *thread_args = malloc(nthreads * sizeof(struct t_arg));
    
    
    // init mutex
    
    ret = pthread_mutex_init(&mutex, NULL); //initialize the mutex
        if (ret) print_error("ERROR: pthread_mutex_init failed");
    
    
    // create random number array
    
    int i;
    srand((unsigned)time(0));
    printf(">> random array versus results\n\n");
    for (i = 0; i < length; i++)
    {
        rand_array[i] = rand() % MAX;
        printf("%d\t", rand_array[i]);
    }
    printf("\n");
    
    
    // populate thread arguments for all the threads
    for (i = 0; i < nthreads; i++)
    {
        thread_args[i].array = rand_array;
        thread_args[i].length = length;
        thread_args[i].s = s;
        thread_args[i].numthreads = nthreads;
        thread_args[i].id = i;
    }
    
    // create threads
    
    for (i = 0; i < nthreads; i++)
    {
        ret = pthread_create( &thread_array[i], NULL, scalar_multiply, &thread_args[i]);
        if (ret) print_error("ERROR: pthread_create failed");
    }

    
    // join threads
    
    for (i = 0; i < nthreads; i++)
    {
        ret = pthread_join(thread_array[i], NULL);
        if (ret) print_error("ERROR: pthread_join failed");
    }
    
    
    // print results
    
    for (i = 0; i < length; i++)
    {
        printf("%d\t", rand_array[i]);
    }
    printf("\n\n");
    
    // free space
    
    free(thread_array);
    free(rand_array);

    
    return 0;
}

