
#include "m-set-parallelized.h"

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
 * print error message and exit
 * @param msg message to be printed before exiting
*/
int print_error(char* msg)
{
    fprintf(stderr, "%s\n", msg);
    exit(2);
}


int main(int argc, char **argv)
{
    
    // gif rendering
    CGIF*             pGIF;           // struct containing the GIF
    CGIF_Config       gConfig;        // global configuration parameters for the GIF
    CGIF_FrameConfig  fConfig;        // configuration parameters for a frame
    uint8_t           numColors = 14;  // number of colors in aPalette (up to 256 possible)
    uint8_t           aPalette[] = {0x00, 0x00, 0x00,       // black
                                    0x06, 0xdb, 0x0b,       // neon green
                                    0x00, 0xc9, 0x6b,
                                    0x00, 0xb3, 0x91,
                                    0x00, 0x9a, 0x99,       // bright teal
                                    0x00, 0x80, 0xaf,
                                    0x00, 0x60, 0xc0,
                                    0x07, 0x2d, 0xab,       // blue
                                    0x1b, 0x1e, 0x7e,
                                    0x19, 0x12, 0x54,
                                    0x13, 0x02, 0x2e,       // midnight
                                    0x0c, 0x01, 0x1c,
                                    0x0b, 0x01, 0x13,
                                    0x00, 0x00, 0x00};      // black
    
    // random number generator
    time_t t;
    srand((unsigned) time(&t));
    
    if (argc != 5)
    {
        fprintf(stderr, "usage: %s <n> <d> <i>\n", argv[0]);
        fprintf(stderr, "       where <n> is the number of threads\n");
        fprintf(stderr, "             <d> is the length of the sides in pixels\n");
        fprintf(stderr, "             <u> is upper bound of the set\n");
        fprintf(stderr, "             <l> is lower bound of the set\n");
        return 1;
    }
    
    // store user inputs
    nthreads = (int)strtol(argv[1], NULL, 10);
    if (nthreads < 1) print_error("ERROR: need a positive number of threads");
    
    h = (int)strtol(argv[2], NULL, 10);
    w = (int)strtol(argv[2], NULL, 10);
    if (h < nthreads) print_error("ERROR: length must be greater than nthreads");
    
    upper = strtof(argv[3], NULL);
    lower = strtof(argv[4], NULL);
    if (lower >= upper) print_error("ERROR: lower bound must be smaller than upper bound.");
    
    // allocate memory for image data
    pImageData = malloc(w * h * sizeof(uint8_t));
    if(!pImageData) print_error("ERROR malloc failed at pImageData");
    
    // initialize the GIF-configuration and create a new GIF
    initGIFConfig(&gConfig, "m-set.gif", w, h, aPalette, numColors);
    pGIF = cgif_newgif(&gConfig);
    
    int x_i, y_i;
    float x, xx, y, cx, cy;
    
    float range = upper - lower;
    int i, n;
    
#pragma omp parallel for num_threads(nthreads) schedule(static, 3) \
    default(none) shared(pImageData, range, w, h, lower) \
    private(i, y_i, x_i, y, x, cy, cx, xx, n)
    for (i = 0; i < h * w; i++)
    {
        y_i = i / w;
        x_i = i % w;
        
        y = (((float)y_i / (float)h) * range) + lower;
        x = (((float)x_i / (float)w) * range) + lower;
        
        cy = y;
        cx = x;
        
        n = 0;
        while (n < ITER_MAX)
        {
            xx = x * x - y * y + cx;
            y = 2 * x * y + cy;
            x = xx;
            
            if ((x + y) > 16)
            {
                break;
            }
            n++;
        }
        
        // 5 colors outside of center black
        
        if (n == ITER_MAX)
        {
            pImageData[i] = 0;
        }
        else
        {
            // pImageData[i] = 1;
            pImageData[i] = 13 - (int)(((float)n / (float)ITER_MAX) * 13);
        }
    }
    
    // initialize the frame-configuration
    initFrameConfig(&fConfig, pImageData);
    
    // add a new frame to the GIF
    cgif_addframe(pGIF, &fConfig);
    
    // free image data when frame is added
    free(pImageData);
    
    // close GIF and free allocated space
    cgif_close(pGIF);
    
    return 0;
}
