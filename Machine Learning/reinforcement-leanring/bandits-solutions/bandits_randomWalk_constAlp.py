'''
k_armed_bandit_skeleton2.py
k-armed bandit problem
generate k bandits
each instance has a true reward q chosen from N[0,1]
each instance will return reward q + e, where
e is chosen from a normal distrib with mean 0 and sd 1 (N[0,1])
goal: test epsilon-greedy with epsilon = 0, 0.01, 0.1
number of steps/actions = 1000
number of runs = 2000

add separate plotting function to show the average rewards over
time, use matplotlib
'''
import math
import numpy as np
import matplotlib.pyplot as plt

class Bandit:
    '''
    this class should handle the true value
    and standard deviation for each bandit
    and return a reward based on them

    list of k average rewards
    list of k counts for each bandit
    '''

    def __init__(self, _true_value=0.0, _sd=1):
        self.q = _true_value
        self.initial_q = _true_value
        self.sd = _sd

    def reward(self):
        r = self.q + self.sd * np.random.randn()
        return r

    def reset_q(self):
        self.q = self.initial_q

    def get_q(self):
        return self.q

    def update_reward(self):
        # random walk
        r = np.random.randint(0, 2)
        if r == 0:
            self.q -= 0.05
        else:
            self.q += 0.05


class K_bandits:
    '''
    list of k Bandits

    methods: choose action, reset list
    '''

    def __init__(self, _k=10, _runs=2000, _steps=1000, _eps=0, _alp=1):

        self.k = _k
        self.runs = _runs
        self.steps = _steps
        self.eps = _eps
        self.alp = _alp
        self.bandits = []

        # create k instances of Bandits
        for i in range(k):
            q = np.random.randn()
            b = Bandit(_true_value=q)
            self.bandits.append(b)


    # simulate action selections and learning that occurs in a run
    def simulate(self):

        r_sum = 0
        q_est_mean = [0] * k
        k_cnts_mean = [0] * k
        r_record_mean = [0] * steps

        # track first 20, 50, 100, 200, and 400 steps' mean reward
        r_per_run = []
        step_marks = [0, int(steps / 100), int(steps / 40), int(steps / 20), int(steps / 10), int(steps / 5)]
        length = len(step_marks) - 1
        r_initial_mean = [[0.0] * runs for i in range(length)]

        # track mean final true value
        fin_q_mean = [0.0] * k

        for run in range(self.runs):

            q_estimates = np.zeros(self.k)
            r_record = np.zeros(self.steps)
            cnts = np.zeros(self.k)

            for step in range(self.steps):

                rand = np.random.rand()

                if rand < self.eps:
                    # explore
                    current_k = np.random.randint(0, self.k - 1)
                    q_estimates, r, cnts = \
                        self.pull_handle(q_estimates, current_k, cnts)

                else:
                    # exploit
                    max = np.amax(q_estimates)
                    k_list = []

                    for i in range(q_estimates.size):
                        if q_estimates[i] == max:
                            k_list.append(i)

                    # this ensures that ties are broken at random, instead of
                    # always selecting first index where value is matched
                    if len(k_list) > 1:
                        current_k = k_list[np.random.randint(
                            low=0, high=len(k_list) - 1
                        )]
                    else:
                        current_k = k_list[0]

                    q_estimates, r, cnts = \
                        self.pull_handle(q_estimates, current_k, cnts)

                r_record[step] = r

            # get mean initial reward from r_record
            if self.eps == 0.01 and track_initial:

                mean, sum = 0, 0

                for n in range(length):
                    start = step_marks[n]
                    stop = step_marks[n + 1]
                    sum += np.sum(r_record[start:stop])
                    mean = (sum / stop)
                    r_initial_mean[n][run] = mean

                r_per_run.append(np.sum(r_record))

            # update mean value of estimated q of each bandit
            for n in range(q_estimates.size):
                q_est_mean[n] = update_mean(q_est_mean[n], q_estimates[n], run)

            # update sum if rewards this run (for calculating average at the end)
            r_sum += np.sum(r_record)

            # update the mean of rewards from step 0 to 1999
            for n in range(len(r_record_mean)):
                r_record_mean[n] = update_mean(r_record_mean[n], r_record[n], run)

            k_cnts_mean = [update_mean(k_cnts_mean[a], cnts[a], run) for a in range(cnts.size)]

            # collect true value at end of this run
            fin_q = [self.bandits[a].get_q() for a in range(len(self.bandits))]

            # update mean of true value of bandits with final true value of this run
            fin_q_mean = [update_mean(fin_q_mean[a], fin_q[a], run) for a in range(len(fin_q))]

        avg_r = rd(r_sum / runs)
        r_sum = rd(r_sum)
        print_result(self.alp, self.eps, q_est_mean, r_record_mean, avg_r, r_sum)

        if self.eps == 0.01 and track_initial:
            # plot how rewards of the initial N steps correlates to overall reward
            plot_initial_impact(r_initial_mean, r_per_run, step_marks)


        return k_cnts_mean, fin_q_mean, q_est_mean, r_record_mean, r_sum


    # restore true value of bandits to their initial value
    def restore_bandits(self):
        for i in range(len(self.bandits)):
            self.bandits[i].reset_q()


    # get reward from selected bandit, update
    # counts and mean value of selected bandit
    def pull_handle(self, _q_estimates, _k, _cnts):

        _cnts[_k] += 1

        bandit = self.bandits[_k]
        r = bandit.reward()

        # make a 0.05 increment in a random direction
        for n in range(len(self.bandits)):
            self.bandits[n].update_reward()

        _q_estimates[_k] = \
            update_mean_const_alp(_q_estimates[_k], r, self.alp)

        return _q_estimates, r, _cnts

    def set_alp(self, _alp):
        self.alp = _alp

    def set_eps(self, _eps):
        self.eps = _eps

# standard rounding for matplotlib graphs
rd = lambda x: round(x, 2)

def print_result(_alp, _eps, q_est_mean, r_record_mean, avg_r, r_sum):

    np.set_printoptions(formatter={'float_kind': "{:.2f}".format})

    print(f"\n\nALPHA={_alp}, EPS={_eps}\n" +
        f"total reward: {r_sum}\n" +
        f"avg. reward / run: {avg_r}\n\n" +
        f"mean q_estimates: ")
    print(["{0:0.2f}".format(i) for i in q_est_mean])
    print("mean reward for rhis run per step: ")
    print(["{0:0.2f}".format(i) for i in r_record_mean])
    print("----------------------------------------------------------")


def plot_cnts_vs_q(_k_cnts, _init_q, _fin_q, _q_est, _alp, _eps):

    fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(18, 6))
    fig.suptitle(
        f"Value-action estimates and k-arm frequency of bandits (alpha={_alp}, eps={_eps})"
    )

    x = np.arange(1, 11)
    ax1.set_xticks(x)
    ax2.set_xticks(x)
    ax1.set_xlabel("Bandits")
    ax2.set_xlabel("Bandits")
    ax1.set_ylabel('Action value')
    ax2.set_ylabel('Number of times bandit was used')

    ax1.plot(x, _init_q, ':g>', label='init. value')
    ax1.plot(x, _fin_q, ':r<', label='final value')
    ax1.plot(x, _q_est, ':bo', label='value est.')

    ax2.bar(x, _k_cnts, color='blue')

    handles, labels = ax1.get_legend_handles_labels()
    plt.legend(handles, labels, bbox_to_anchor=(-1.5, 0), loc='lower left')
    plt.show()


def plot_rewards(_r_means, _x_limit, _labels, _alp):

    c = ['#555b6e','#c8553d', '#2a9d8f']
    x = np.arange(0, _x_limit, 1)

    for i in range(len(_labels)):
        plt.plot(x, _r_means[i], linewidth=0.5,label=f"{_labels[i]}", c=c[i])

    plt.xlabel('steps')
    plt.ylabel('rewards')
    plt.title(f"Average rewards per step, alpha={_alp}")
    plt.legend()
    plt.show()


def plot_initial_impact(_r_init_mean, _r_per_run, _step_marks):

    for i in range(len(_step_marks) - 1):

        arr = _r_init_mean[i]
        sorted = np.argsort(arr)
        x_range = []
        r_sorted = []

        for j in sorted:
            # creating x tick labels, arranged from lowest to highest mean values
            x_range.append(arr[j])
            r_sorted.append(_r_per_run[j])

        fig = plt.figure()
        ax = plt.gca()

        fig.suptitle(f"Impact of the first N steps on overall reward of the run (N = {_step_marks[i + 1]})")
        plt.xlabel("Mean value of the first N steps")
        plt.ylabel("Total rewards in run")

        mid = math.floor(len(x_range) / 2)
        end = len(x_range) - 1
        x_tick_pos = [0, mid, end]
        x_labels = [rd(x_range[0]), rd(x_range[mid]), rd(x_range[end])]
        plt.xticks(x_tick_pos, x_labels)

        x = np.arange(0, len(arr))
        plt.plot(x, r_sorted, linewidth=0.5)

        plt.show()


def update_mean_const_alp(_mean, _val, _alp):
    return _mean + (_alp * (_val - _mean))


def update_mean(_mean, _val, _run_cnt):
    if _run_cnt == 0:
        return 0
    return _mean + ((_val - _mean) / _run_cnt)


def get_mean(_arr):
    return np.sum(_arr) / len(_arr)


if __name__ == '__main__':

    # set to True to track initial N steps' mean rewards
    track_initial = False

    k = 10
    steps = 10000
    runs = 1000

    epsilons = [0, 0.1, 0.01]
    alphas = [0.2, 0.1, 0.01]

    # create k bandit objects
    bandits = \
        K_bandits(
            _k=k,
            _runs=runs,
            _steps=steps
        )

    init_q = [b.get_q() for b in bandits.bandits]

    for i in range(len(alphas)):

        bandits.set_alp(alphas[i])

        r_record_mean_per_eps = [
            [0.0] * steps for i in range(len(epsilons))
        ]

        for j in range(len(epsilons)):

            bandits.set_eps(epsilons[j])

            # simulate steps in run j
            k_cnts_mean, fin_q_mean, q_est_mean, r_record_mean, r_sum = bandits.simulate()

            # store documented mean value of reward record for the 1000 runs
            r_record_mean_per_eps[j] = r_record_mean

            # plot k-counts versus their estimated true value in two side-by-side charts
            plot_cnts_vs_q(k_cnts_mean, init_q, fin_q_mean, q_est_mean, alphas[i], epsilons[j])

            # restoring true value of each bandits to it's initial value
            bandits.restore_bandits()

        # plot mean reward progression for all eps values
        plot_rewards(r_record_mean_per_eps, steps, epsilons, alphas[i])




