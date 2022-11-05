#include "Trap_parallized.h"

double f(double x)
{
    //return pow(x, 5)- (8 * pow(x, 3)) + (10 * x) + 6;
    return x * x;
}

void Para_Trap(double a, double b, int n, double* global_res);
double Serial_Trap(double a, double b, int n);
void printRes(int n, double a, double b, double p_res, double ps_res, double s_res, float p, float ps, float s, int core_num);

int main(int argc, char* argv[])
{
    double global_res_p = 0.0;
    double global_res_ps = 0.0;
    double a, b;
    int i, n;
    int thread_cnt;
    
    thread_cnt = (int)strtol(argv[1], NULL, 10);
    printf("Enter a, b, and n\n");
    scanf("%lf %lf %d", &a, &b, &n);
    
    // time value struct from time.h
    struct timeval p_start, p_end, ps_start, ps_end, s_start, s_end;
    
    // start time for parallel program
    gettimeofday(&p_start, NULL);
    
    double h = (b - a) / n;
    
    #pragma omp parallel for num_threads(thread_cnt) schedule(dynamic, 2)
    for (i = 0; i <= n; i++)
    {
        double start = a + (i * h);
        double end = start + h;
        double area = ((f(start) + f(end)) * h) / 2.0;
        
        #pragma omp critical
        global_res_p += area;
    }
    
    // end time for parallel program
    gettimeofday(&p_end, NULL);
    
    
    
    // start time for parallel program
    gettimeofday(&ps_start, NULL);
    
    #pragma omp parallel num_threads(thread_cnt)
    Para_Trap(a, b, n, &global_res_ps);
    
    // end time for parallel program
    gettimeofday(&ps_end, NULL);
    
    
    
    // end time for parallel program
    gettimeofday(&s_start, NULL);
    
    // Serial implementation
    double serial_res = Serial_Trap(a, b, n);
    
    // end time for parallel program
    gettimeofday(&s_end, NULL);
    
    
    
    float p_duration = ((p_end.tv_sec * 1000000 + p_end.tv_usec) -
                        (p_start.tv_sec * 1000000 + p_start.tv_usec));
    float ps_duration = ((ps_end.tv_sec * 1000000 + ps_end.tv_usec) -
                        (ps_start.tv_sec * 1000000 + ps_start.tv_usec));
    float s_duration = ((s_end.tv_sec * 1000000 + s_end.tv_usec) -
                       (s_start.tv_sec * 1000000 + s_start.tv_usec));
    
    printRes(n, a, b, global_res_p, global_res_ps, serial_res, p_duration, ps_duration, s_duration, thread_cnt);
    
    return 0;
}

void printRes(
              int n, double a, double b,
              double p_res, double ps_res, double s_res,
              float p, float ps, float s,
              int core_num
              )
{
    printf("\n\nWith n = %d trapezoids, area under function from %2f to %2f: \n\n", n, a, b);
    printf(">> serial:                                  %5f\n", s_res);
    printf(">> parallel:                                %5f\n", ps_res);
    printf(">> parallel for (dynamically scheduled):    %5f\n\n", p_res);
    
    float speedup = s / p;
    float eff = speedup / core_num;
    
    printf("\nSTATS for estimating trapozoid area with %d threads using OpenMP: \n\n", core_num);
    printf(">> serial:                                  %d micro seconds\n", (int)s);
    printf(">> parallel:                                %d micro seconds\n", (int)ps);
    printf(">> parallel for (dynamically scheduled):    %d micro seconds\n\n", (int)p);
    printf(">> speedup:                                 %.5f\n", speedup);
    printf(">> efficiency:                              %.5f\n\n", eff);
}

double Serial_Trap(double a, double b, int n)
{
    double loc_a, loc_b, loc_area, res;
    double h = (b - a) / n;
    
    res = 0.0;
    for (int i = 0; i <= n; i++)
    {
        loc_a = a + (i * h);
        loc_b = loc_a + h;
        loc_area = ((f(loc_a) + f(loc_b)) * h) / 2.0;
        res += loc_area;
    }
    
    return res;
}

void Para_Trap(double a, double b, int n, double* global_res)
{
    int i, start, end;
    
    int my_rank = omp_get_thread_num();
    int thread_cnt = omp_get_num_threads();
    int chunk = n / thread_cnt;
    int remainder = n % thread_cnt;
    
    double h = (b - a) / n;
    if (remainder != 0)
    {
        start = chunk * my_rank;
        end = start + chunk;
    }
    else
    {
        if (remainder > my_rank)
        {
            start = (chunk + 1) * my_rank;
            end = start + (chunk + 1);
        }
        else
        {
            start = ((chunk + 1) * remainder) + (chunk * (my_rank - remainder));
            end = start + chunk;
        }
    }
    
    double res = 0.0;
    for (i = start; i < end; i++)
    {
        double a_i = a + (i * h);
        double b_i = a_i + h;
        
        double area = ((f(a_i) + f(b_i)) / 2.0) * h;
        res += area;
    }
    
    #pragma omp critical
    *global_res += res;
}
