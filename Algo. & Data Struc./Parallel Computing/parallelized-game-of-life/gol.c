#include "gol.h"
#include "./cgif.h"

/* Small helper functions to initialize GIF- and frame-configuration */
static void initGIFConfig(
                          CGIF_Config* pConfig,
                          char* path,
                          uint16_t width,
                          uint16_t height,
                          uint8_t* pPalette,
                          uint16_t numColors
                          )
{
    memset(pConfig, 0, sizeof(CGIF_Config));
    pConfig->width                   = width;
    pConfig->height                  = height;
    pConfig->pGlobalPalette          = pPalette;
    pConfig->numGlobalPaletteEntries = numColors;
    pConfig->path                    = path;
}
static void initFrameConfig(CGIF_FrameConfig* pConfig, uint8_t* pImageData)
{
    memset(pConfig, 0, sizeof(CGIF_FrameConfig));
    pConfig->pImageData = pImageData;
}

/**
 * bound index to make end indices connect
 * @param index a position in the array
*/
int boundIndex(int index) {
    if (index < 0) {
        return (dim_w * dim_h) - index;
    } else if (index >= dim_w * dim_h) {
        return index - (dim_w * dim_h);
    }
    return index;
}

/**
 * counts the neighbors of a cell
 * @param pImageData a 1D Array of size WIDTH * HEIGHT
 * @param index the position we are counting neigbors for
*/
int countNeighbors(uint8_t* pImageData, int index) {
    int sum = 0;
    for (int i = -1; i < 2; i++) {
        for (int j = -1; j < 2; j++) {
            sum += pImageData[boundIndex(index + (i * dim_w) + j)];
        }
    }
    sum -= pImageData[index];
    return sum;
}

/**
 * computes the next generation of cells following the game's rules
 * @param args contains thread id
*/
void *threaded_computeNewGen(void* args) {
    struct t_arg* my_arg = (struct t_arg*) args;
    
    int my_id = my_arg->thread_id;
    
    int task_size = dim_h / nthreads;
    int remainder = dim_h % nthreads;
    int start_row, end_row;
    
    if (remainder > my_id) {
        start_row = (task_size + 1) * my_id;
        end_row = start_row + (task_size + 1);
    } else {
        start_row = ((task_size + 1) * remainder) + (task_size * (my_id - remainder));
        end_row = start_row + task_size;
    }
    for (int i = start_row; i < end_row; i++) {
        for (int j = 0; j < dim_w; j++) {
            
            int index = i * dim_w + j;
            int state = pImageData[index];
            int neighbors = countNeighbors(pImageData, index);
            
            if (state == 0 && neighbors == 3) {
                nextImageData[index] = 1;
            } else if (state == 1 && (neighbors < 2 || neighbors > 3)) {
                nextImageData[index] = 0;
            } else {
                nextImageData[index] = pImageData[index];
            }
        }
    }
    return NULL;
}

/**
 * print error message and exit
 * @param msg message to be printed before exiting
*/
int print_error(char* msg)
{
    fprintf(stderr, "%s\n", msg);
    exit(2);
}

int main(int argc, char **argv) {
    
    // gif rendering
    CGIF*             pGIF;           // struct containing the GIF
    CGIF_Config       gConfig;        // global configuration parameters for the GIF
    CGIF_FrameConfig  fConfig;        // configuration parameters for a frame
    uint8_t           numColors = 2;  // number of colors in aPalette (up to 256 possible)
    uint8_t           aPalette[] = {0xFF, 0x00, 0x00,     // red
                                    0x00, 0x00, 0x00};    // blue
    
    // random number generator
    time_t t;
    srand((unsigned) time(&t));
    
    // error catching
    int ret;
    
    if (argc !=4)
    {
        fprintf(stderr, "usage: %s <n> <p> <d>\n", argv[0]);
        fprintf(stderr, "       where <n> is the number of threads\n");
        fprintf(stderr, "             <p> is the length of the sides in pixels\n");
        fprintf(stderr, "             <d> is the density of the initial population in range of 1 - 100 (percent)\n");
        return 1;
    }
    
    // store user inputs
    nthreads = (int)strtol(argv[1], NULL, 10);
    if (nthreads < 1) print_error("ERROR: need a positive number of threads");
    
    int dim = (int)strtol(argv[2], NULL, 10);
    if (dim < nthreads) print_error("ERROR: length must be greater than nthreads");
    
    dim_w = dim;
    dim_h = dim;
    
    int density = (int)strtol(argv[3], NULL, 10);
    printf("\n>> %d x %d grid populated with density of %d with %d cores\n", dim, dim, density, nthreads);
    
    pthread_t* thread_array = malloc(nthreads * sizeof(pthread_t));
    if (!thread_array) print_error("ERROR: malloc failed at thread_array");
    
    struct t_arg *thread_args = malloc(nthreads * sizeof(struct t_arg));
    if (!thread_args) print_error("ERROEL malloc failed at thread_args");
    
    // allocate memory for image data
    pImageData = malloc(dim_w * dim_h * sizeof(uint8_t));
    if(!pImageData) print_error("ERROR malloc failed at pImageData");
    nextImageData = malloc(dim_w * dim_h * sizeof(uint8_t));
    if(!nextImageData) print_error("ERROR malloc failed at nextImageData");
    
    // initialize the GIF-configuration and create a new GIF
    initGIFConfig(&gConfig, "gol.gif", dim_w, dim_h, aPalette, numColors);
    pGIF = cgif_newgif(&gConfig);
    
    for (int i = 0; i < (dim_w * dim_h); i++) {
        int random = rand() % 100;
        pImageData[i] = (random < density) ? 1 : 0;
    }
    
    // init mutex
    ret = pthread_mutex_init(&mutex, NULL); //initialize the mutex
        if (ret) print_error("ERROR: pthread_mutex_init failed");
    
    for (int i = 0; i < nthreads; i++) {
        thread_args[i].thread_id = i;
    }
    
    int time_step = 0;
    while(time_step < 100) {
        // add frame to GIF
        // initialize the frame-configuration
        initFrameConfig(&fConfig, pImageData);
        
        // add a new frame to the GIF
        cgif_addframe(pGIF, &fConfig);
        
        for (int i = 0; i < nthreads; i++) {
            thread_args[i].thread_id = i;
            ret = pthread_create(&thread_array[i], NULL, threaded_computeNewGen, &thread_args[i]);
            if (ret) print_error("ERROR: pthread_create failed");
        }
        
        for (int i = 0; i < nthreads; i++)
        {
            ret = pthread_join(thread_array[i], NULL);
            if (ret) print_error("ERROR: pthread_join failed");
        }
        
        pImageData = nextImageData;
        time_step += 1;
    }
    
    // free image data when frame is added
    free(thread_args);
    free(thread_array);
    free(pImageData);
    free(nextImageData);

    // close GIF and free allocated space
    cgif_close(pGIF);
    
    return 0;
}
