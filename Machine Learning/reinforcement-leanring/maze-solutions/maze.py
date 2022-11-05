import numpy as np
import matplotlib.pyplot as plt
from matplotlib.table import Table

def init_maze(_w, _h):
    return np.full((_h, _w), -1)

def show_maze(_maze):

    fig, ax = plt.subplots()
    ax.set_axis_off()
    table = Table(ax, bbox=[0, 0, 1, 1])
    c = ['#FFFFFF', '#000000']

    for i in range(_maze.shape[0]):
        for j in range(_maze.shape[1]):

            table.add_cell(
                i, j, 0.2, 0.2, text=_maze[i, j], loc='center'
            )
            table[(i, j)].set_facecolor(c[_maze[i, j]])
    table.auto_set_font_size(False)
    ax.add_table(table)

    plt.suptitle(f"Maze ")
    plt.show()

def get_start_i(_n):
    start_i = np.random.randint(_n, size=1)[0]
    start_i = start_i if start_i != 0 else 1
    return start_i if start_i != _n - 1 else _n - 2

def cnt_neighbors(_mz, _wall):
    cnt = 0
    cnt = cnt + 1 if _mz[_wall[0]-1, _wall[1]] == 0 else cnt
    cnt = cnt + 1 if _mz[_wall[0]+1, _wall[1]] == 0 else cnt
    cnt = cnt + 1 if _mz[_wall[0], _wall[1]-1] == 0 else cnt
    cnt = cnt + 1 if _mz[_wall[0], _wall[1]+1] == 0 else cnt
    return cnt

def rm_wall(_wall, _walls):
    for i in range(_walls.shape[0]):
        current = _walls[i]
        print(f'current = {current}')
        if current[0] == _wall[0] and current[1] == _wall[1]:
            print(f'_walls before rm_wall = {_walls}')
            return np.delete(_walls, i, axis=0)
    return _walls

def mk_neighbors_wall(_mz, _wall, _walls):

    def add_wall(_mz, _y, _x, _walls):
        if _mz[_y, _x] < 0:
            _mz[_y, _x] = 1
            return np.insert(_walls, 0, [_y, _x], 0)
        return _walls

    if wall[0] != 0:
        _walls = add_wall(_mz, wall[0] - 1, wall[1], _walls)

    if wall[0] != h - 1:
        _walls = add_wall(_mz, wall[0] + 1, wall[1], _walls)

    if wall[1] != 0:
        _walls = add_wall(_mz, wall[0], wall[1] - 1, _walls)

    if wall[1] != w - 1:
        _walls = add_wall(_mz, wall[0], wall[1] - 1, _walls)

    return _walls



# START OF PROGRAM ------------------------------------------------
if __name__ == '__main__':

    h, w = 15, 15

    # -1: unvisited, 0: pathway, 1: wall
    mz = init_maze(30, 30)

    start_h = get_start_i(h)
    start_w = get_start_i(w)

    mz[start_h, start_w] = 0
    walls = np.array([[start_h - 1, start_w],
                      [start_h, start_w - 1],
                      [start_h, start_w + 1],
                      [start_h + 1, start_w]])
    for i in range(4):
        y, x = walls[i, 0], walls[i, 1]
        mz[y, x] = 1

    t = 0
    while walls.shape[0] != 0:
        print(f't = {t}, walls = {walls}')
        # process a random wall
        rand = np.random.randint(walls.shape[0])
        wall = walls[rand]
        print(f'rand = {rand}')
        print(f'wall={wall}')

        if wall[0] != 0 and wall[0] != h - 1 and wall[1] != 0 and wall[1] != w - 1:
            if (mz[wall[0] - 1, wall[1]] < 0 and mz[wall[0] + 1, wall[1]] == 0) or \
                    (mz[wall[0] + 1, wall[1]] < 0 and mz[wall[0] - 1, wall[1]] == 0) or \
                    (mz[wall[0], wall[1] - 1] < 0 and mz[wall[0], wall[1] + 1] == 0) or \
                    (mz[wall[0], wall[1] + 1] < 0 and mz[wall[0], wall[1] - 1] == 0):
                print(f'at least 1/4 conditions met')
                cell_cnt = cnt_neighbors(mz, wall)
                print(f'cell_cnt = {cell_cnt}')
                if cell_cnt < 2:
                    mz[wall[0], wall[1]] = 0
                    walls = rm_wall(wall, walls)
                    print(f'_walls after rm_wall = {walls}')
                    walls = mk_neighbors_wall(mz, wall, walls)
                    print(f'_walls after mk_neigh = {walls}')
        t += 1

    # fill all unvisited cell as wall
    for (i, j), state in np.ndenumerate(mz):
        if state < 0:
            mz[i, j] = 1

    # make entrance and exit
    ent, ext = np.array([0, 0]), np.array([h - 1, 0])
    for i in range(w):
        if mz[1, i] == 0:
            mz[0, i] = 0
            ent[1] = i
            break
    for i in range(w - 1, 0, -1):
        if mz[h - 2, i] == 0:
            mz[h - 1, i] = 0
            ext[1] = i
            break

    # set agent position at entrance
    agent_pos = ent

    # show maze
    fig, ax = plt.subplots()
    ax.set_axis_off()
    table = Table(ax, bbox=[0, 0, 1, 1])
    c = ['black', 'darkgreen', 'lime']

    for (i, j), cell in np.ndenumerate(mz):
        table.add_cell(
            i, j, 0.2, 0.2, text='', loc='center'
        )
        ci = 1 if mz[i, j] == 1 else 0
        ci = 2 if np.all(agent_pos, [i, j]) else ci
        table[(i, j)].set_facecolor(c[ci])
    ax.add_table(table)
    plt.show()



