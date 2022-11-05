
#include "quicksort-parallelized.h"


/*printing rand_array*/
void print_arr(long *rand_arr)
{
    for (int i = 0; i < length; i++)
    {
        printf("%ld ", rand_arr[i]);
    }
    printf("\n");
}

/*error handling function: prints out error message*/
int print_error(char *msg) {
    fprintf(stderr, "%s\n", msg);
    exit(2);
}

// function executed by each thread
void *quick_sort(void *args)
{
    
    struct t_arg *myargs = (struct t_arg *)args;
    long my_hi = myargs->hi;
    long my_lo = myargs->lo;
    
    long i, j, pivot, temp;
    
    if (my_lo < my_hi)
    {
        pivot = my_lo;
        i = my_lo;
        j = my_hi;
        
        while(i < j)
        {
            while(rand_array[i] <= rand_array[pivot] && i < my_hi)
                i++;
            while(rand_array[j] > rand_array[pivot] && j >= 0)
                j--;
            if(i < j){
                temp = rand_array[i];
              
                pthread_mutex_lock(&mutex);
              
                rand_array[i] = rand_array[j];
                rand_array[j] = temp;
              
                pthread_mutex_unlock(&mutex);
            }
        }
        temp = rand_array[pivot];
      
        pthread_mutex_lock(&mutex);
         
        rand_array[pivot] = rand_array[j];
        rand_array[j] = temp;
        
        pthread_mutex_unlock(&mutex);
        
        // spawn new threads
        pthread_t thread_0, thread_1;
        struct t_arg arg_0 = {.lo = my_lo, .hi = j - 1};
        struct t_arg arg_1 = {.lo = j + 1, .hi = my_hi};
        
        pthread_create(&thread_0, NULL, quick_sort, &arg_0);
        pthread_create(&thread_1, NULL, quick_sort, &arg_1);
        
        pthread_join(thread_0, NULL);
        pthread_join(thread_1, NULL);
    }

    return NULL;
}

int main(int argc, char **argv)
{
    pthread_t *main_thread = malloc(sizeof(pthread_t));

    // display help messaging if incorrect number of arguments detected
    if (argc != 2) {
        fprintf(stderr, "usage: %s <p>\n", argv[0]);
        fprintf(stderr, "       <p> is the length of array to be sorted\n");
        return 1;
    }
    
    // store user inputs
    length = strtol(argv[1], NULL, 10);
    
    // allocate space
    rand_array = malloc(length * sizeof(long));
    if (!rand_array) print_error("ERROR: malloc failed");
    // create shuffled array
    
    long i;
    srand((unsigned)time(0));
    for (i = 0; i < length; i++)
    {
        rand_array[i] = i;
    }
    for (i = length - 1; i > 0; i--)
    {
        long j = rand() % length;
        long temp = rand_array[i];
        rand_array[i] = rand_array[j];
        rand_array[j] = temp;
    }
    
    print_arr(rand_array);
    
    struct t_arg main_arg = {.lo = 0, .hi = length - 1};
    
    pthread_create(main_thread, NULL, quick_sort, &main_arg);
    pthread_join(*main_thread, NULL);
    
    print_arr(rand_array);
    
    // free space
    free(main_thread);
    free(rand_array);

    return 0;
}
