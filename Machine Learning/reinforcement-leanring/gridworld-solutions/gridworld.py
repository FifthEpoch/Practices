import matplotlib.pyplot as plt
import numpy as np
import math
from matplotlib.table import Table

rd = lambda x: round(x, 1)

class Cell:

    def __init__(self, _x, _y, _v=0.0):

        self.coord = np.array([_x, _y])
        self.v = _v
        self.q = np.array([0.0] * 4)
        self.pi = np.array([0.25] * 4)
        self.pref = np.array([0.0] * 4)
        self.count = 0
        self.first_visit = True

    def get_count(self):
        return self.count

    def get_first_visit(self):
        return self.first_visit

    def get_q(self):
        return self.q

    def get_pi(self):
        return self.pi

    def get_pref(self):
        return self.pref

    def get_v(self):
        return self.v

    def increment_count(self):
        self.count += 1

    def set_first_visit(self, _bool):
        self.first_visit = _bool

    def set_q(self, _q):
        self.q = _q

    def set_q_i(self, _i, _q):
        self.q[_i] = _q

    def set_pi(self, _pi):
        self.pi = _pi

    def set_pref(self, _pref):
        self.pref = _pref

    def set_v(self, _v):
        self.v = _v


class Grid:

    def __init__(self, _w, _h, _gamma):

        self.gamma = _gamma
        self.w = _w
        self.h = _h

        self.actions = np.array([[0, -1], [0, 1], [-1, 0], [1, 0]])
        self.pi = np.array([[[0.25] * 4 for j in range(_w)] for i in range(_h)])
        self.pref = np.array([[[0.0] * 4 for j in range(_w)] for i in range(_h)])
        self.rand_pi = 1 / len(self.actions)

        self.state_A, self.state_Ap = np.array([0, 1]), np.array([4, 1])
        self.state_B, self.state_Bp = np.array([0, 3]), np.array([2, 3])

        self.grid = np.array([[Cell(i, j) for j in range(_w)] for i in range(_h)])
        self.state = np.array([math.floor(_h / 2), math.floor(_w / 2)])


    def reset_grid(self):
        self.grid = np.array(
            [[Cell(i, j) for j in range(self.w)] for i in range(self.h)]
        )

    def reset_cells(self, _v=0.0):
        for index, cell in np.ndenumerate(self.grid):
            cell.set_v(_v)


    def compute_V(self, _theta=0.01):

        delta = _theta * 2
        while delta > _theta:

            # refresh data memory
            data = self.copy_cell_v()

            # reset variables
            delta = 0

            # looping through all cells on grid
            for (i, j), cell in np.ndenumerate(self.grid):
                v = 0
                old_v = cell.get_v()
                state = np.array([i, j])

                # looping through all available actions
                for action in self.actions:

                    next_state, r = self.get_next_state_n_reward(state, action)
                    x, y = next_state[0], next_state[1]
                    v_sp = data[x, y]
                    v += self.compute_partial_v(r, v_sp)

                # completing the state-value function
                v = self.rand_pi * v
                cell.set_v(v)

                # finding and storing max(delta)
                delta_ij = v - old_v
                delta = delta_ij if delta_ij > delta else delta

            print(f"delta = {delta}")


    def compute_partial_v(self, _r, _v_sp):
        return _r + (self.gamma * _v_sp)


    def copy_cell_v(self):
        data = np.array([[0.0] * self.h for i in range(self.w)])
        for (i, j), cell in np.ndenumerate(self.grid):
            data[i, j] = cell.get_v()
        return data


    def deterministic_pi_improvement(self):

        is_stable = True

        for i in range(self.h):
            for j in range(self.w):

                cell = self.grid[i, j]
                pi_ij = cell.get_pi()
                pi_ij_copy = np.copy(pi_ij)
                max_a_i = self.get_max_a_i(np.array([i, j]))

                for p in range(pi_ij.size):
                    pi_ij[p] = 1 if p == max_a_i else 0

                cell.set_pi(pi_ij)
                is_stable = is_stable if np.all(pi_ij_copy == pi_ij) else False

        if not is_stable:
            print(f"_not_stable_loop_entered")
            self.compute_V()
            self.deterministic_pi_improvement()


    def deterministic_pi_iteration(self):
        self.compute_V()
        self.deterministic_pi_improvement()
        self.show_pi_n_v()


    def deterministic_pi(self, _theta=0.01, _steps=100):

        delta = _theta * 2
        while delta > _theta:

            data = self.copy_cell_v()

            for i in range(_steps):

                x, y = self.state[0], self.state[1]
                pi_xy = self.grid[x, y].get_pi()
                action = np.where(pi_xy == np.amax(pi_xy))[0]

                # get a random action from the policy's optimal action(s)
                next_state, r = self.get_next_state_n_reward(
                    _state=self.state,
                    _action=action[np.random.randint(0, action.size)]
                )

                # update value estimate of current state
                self.update_v(self.state, next_state, r)

                # update policy based on new value
                self.deterministic_pi_iteration()

                # move to the next state
                self.state = next_state

            # find max(delta) after N steps
            new_data = self.copy_cell_v()
            data = np.subtract(new_data, data)
            data = np.absolute(data)
            delta = np.amax(data)

        self.show_pi_n_v()


    def get_max_a_i(self, _state):

        # maximum value of future states
        max_v = 0
        # index of action that gets us to the state with maximum rewards
        max_a_i = 2

        for i in range(4):

            next_state, r = \
                self.get_next_state_n_reward(
                    _state=_state,
                    _action=self.actions[i]
                )

            u, v = next_state[0], next_state[1]
            v_uv = self.grid[u, v].get_v()

            max_a_i = i if v_uv > max_v else max_a_i
            max_v = v_uv if v_uv > max_v else max_v

        return max_a_i


    def get_next_state_n_reward(self, _state, _action):
        if np.all(_state == self.state_A):
            return self.state_Ap, 10
        elif np.all(_state == self.state_B):
            return self.state_Bp, 5
        else:
            next_state = np.add(_state, _action)
            if self.is_in_bounds(next_state):
                return next_state, 0
            else:
                return _state, -1


    def get_rand_action(self):
        rand = np.random.randint(0, 4)
        return self.actions[rand]


    def is_in_bounds(self, _state):
        x, y = _state[0], _state[1]
        if x < 0 or x >= self.h:
            return False
        elif y < 0 or y >= self.w:
            return False
        return True


    def update_v(self, _state, _next_state, _r):

        x, y = _state[0], _state[1]
        u, v = _next_state[0], _next_state[1]

        self.grid[x, y].increment_count()

        partial_v_xy = \
            self.compute_partial_v(
                _r=_r,
                _v_sp=self.grid[u, v].get_v()
            )

        v_xy = self.update_mean(
            _mean=self.grid[x, y].get_v(),
            _val=partial_v_xy,
            _cnt=self.grid[x, y].get_count()
        )

        self.grid[x, y].set_v(v_xy)


    def get_soft_action(self, _pi_xy):
        action_i = np.random.choice(4, 1, p=_pi_xy)[0]
        return action_i


    def update_soft_max(self, _x, _y, _r, _avg_r, _action, _alpha):

        cell = self.grid[_x, _y]
        pi_xy = cell.get_pi()
        pref_xy = cell.get_pref()

        for i in range(pref_xy.size):
            if i == _action:
                # set preference for optimal action
                pref_xy[i] = pref_xy[i] + (_alpha * (_r - _avg_r)*(1 - pi_xy[i]))
            else:
                # set preference for all other actions
                pref_xy[i] = pref_xy[i] - (_alpha * (_r - _avg_r) * pi_xy[i])

        # set policy fot all actions
        exp_pref = [math.exp(pref_xy[i]) for i in range(pref_xy.size)]
        sum_exp_pref = sum(exp_pref)
        for i in range(pi_xy.size):
            pi_xy[i] = exp_pref[i] / sum_exp_pref

        cell.set_pref(pref_xy)
        cell.set_pi(pi_xy)


    def soft_max(self, _init=0.0, _eps=0.01, _theta=0.01, _steps=1000):

        gridworld.reset_cells(_init)

        avg_r = 0
        rounds = 0

        delta = _theta * 2
        while delta > _theta:

            data = self.copy_cell_v()
            rounds += 1

            for i in range(_steps):

                x, y = self.state[0], self.state[1]
                pi_xy = self.grid[x, y].get_pi()

                # get next state and reward
                action = self.get_soft_action(pi_xy)
                next_state, r = self.get_next_state_n_reward(
                    _state=self.state,
                    _action=self.actions[action]
                )

                # update value estimate of current state
                self.update_v(self.state, next_state, r)

                # find action that leads to a future
                # state with the highest value
                max_a_i = self.get_max_a_i(self.state)

                # tracking average reward per step
                avg_r = self.update_mean(avg_r, r, rounds * (_steps + 1))
                self.update_soft_max(x, y, r, avg_r, action, _alpha=0.2)

                self.state = next_state

            # find max(delta) after N steps
            new_data = self.copy_cell_v()
            data = np.subtract(new_data, data)
            data = np.absolute(data)
            delta = np.amax(data)

        self.show_pi_n_v(_init, avg_r)


    def eps_soft(self, _eps=0.01, _episode=100, _steps=20):

        for epi in range(_episode):
            total_r = 0
            for step in range(_steps):

                x, y = self.state[0], self.state[1]
                cell = self.grid[x, y]
                pi_xy = cell.get_pi()

                action = self.get_soft_action(pi_xy)
                next_state, r = self.get_next_state_n_reward(
                    _state=self.state,
                    _action=self.actions[action]
                )
                total_r = (self.gamma * total_r) + r

                # check if this is the first visit
                if cell.get_first_visit():
                    cell.set_q(total_r)
                    cell.set_first_visit(True)

                    q = cell.get_q()
                    max_q = np.where(q == np.amax(q))[0]

                    if max_q.size > 1:
                        current_k = k_list[np.random.randint(
                            low=0, high=len(k_list) - 1
                        )]
                    else:
                        current_k = k_list[0]

                    pi = cell.get_pi()

                    for i in range(4):
                        if i == max_q:
                            pi[i] = 1 - _eps + (_eps / 4)
                        else:
                            pi[i] = _eps / 4




    def monte_carlo_ev(self, _theta=0.01, _steps=1000):

        delta = _theta * 2
        while delta > _theta:

            data = self.copy_cell_v()

            for i in range(_steps):

                # get next state and reward
                next_state, r = self.get_next_state_n_reward(
                    _state=self.state,
                    _action=self.get_rand_action()
                )

                # simulate next state's next state and reward
                next_next_state, next_r = \
                    self.get_next_state_n_reward(
                        _state=next_state,
                        _action=self.get_rand_action()
                    )

                x, y = self.state[0], self.state[1]
                u, v = next_state[0], next_state[1]
                f, g = next_next_state[0], next_next_state[1]

                # increment counts for cell[x, y]
                self.grid[x, y].increment_count()

                # get value estimate of next state
                partial_v_uv = self.compute_partial_v(
                    _r=next_r,
                    _v_sp=self.grid[f, g].get_v()
                )
                v_uv = self.update_mean(
                    _mean=self.grid[u, v].get_v(),
                    _val=partial_v_uv,
                    _cnt=self.grid[u, v].get_count() + 1
                )

                # update value estimate of current state
                partial_v_xy = self.compute_partial_v(r, v_uv)
                v_xy = self.update_mean(
                    _mean=self.grid[x, y].get_v(),
                    _val=partial_v_xy,
                    _cnt=self.grid[x, y].get_count()
                )
                self.grid[x, y].set_v(v_xy)

                # move to next state
                self.state = next_state

            # find max(delta) after N steps
            new_data = self.copy_cell_v()
            data = np.subtract(new_data, data)
            data = np.absolute(data)
            delta = np.amax(data)
            print(f"delta = {delta}")

        self.show_grid()


    def show_grid(self):
        fig, ax = plt.subplots()
        ax.set_axis_off()
        table = Table(ax, bbox=[0, 0, 1, 1])

        for (i, j), cell in np.ndenumerate(self.grid):
            table.add_cell(
                i, j, 0.2, 0.2, text=str(rd(cell.get_v())), loc='center'
            )
        ax.add_table(table)
        plt.show()

    def show_pi_n_v(self, _init=0.0, _avg_r=0.0):

        fig, ax = plt.subplots()
        ax.set_axis_off()
        table = Table(ax, bbox=[0, 0, 1, 1])

        for i in range(self.h):
            for j in range(self.w):

                arrows = ''
                # self.actions = np.array([[0, -1], [0, 1], [-1, 0], [1, 0]])
                dir = {
                    0: '⬅',
                    1: '➡',
                    2: '⬆',
                    3: '⬇',
                }
                pi_ij = self.pi[i, j]
                max_index = np.where(pi_ij == np.amax(pi_ij))[0]

                for k in max_index:
                    arrows += dir.get(k, '')

                arrows += f'\nv ={rd(self.grid[i, j].get_v())}'
                arrows += f'\ncnt ={self.grid[i, j].get_count()}'

                table.add_cell(
                    i, j, 0.2, 0.2, text=arrows, loc='center'
                )

        ax.add_table(table)
        if not(_avg_r == 0.0 and _init == 0.0):
            plt.suptitle(f"avg. reward / step = {rd(_avg_r)}, cell init. = {_init}")
        plt.show()


    def update_mean(self, _mean, _val, _cnt):
        if _cnt == 0:
            return 0
        return _mean + ((_val - _mean) / _cnt)



    # general parameters
    width = 5
    height = 5
    gamma = 0.9
    theta = 0.0001

    # Monte Carlo parameters
    steps = 80

    # init Grid object
    gridworld = Grid(width, height, gamma)

    # getting the true value of each cell
    # gridworld.compute_V(theta)

    # reinitialize all cells
    # gridworld.clear_data()

    # monte carlo method
    # gridworld.monte_carlo_ev(theta, steps)

    # reinitialize all cells
    # gridworld.clear_data()

    # dynamic programming with the Bellman equation
    # gridworld.deterministic_pi_iteration()

    # soft-max
    # gridworld.soft_max( _init=0.0, _theta=0.001, _steps=1000)


