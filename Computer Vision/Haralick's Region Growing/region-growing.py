import numpy as np
import math
import cv2

class Segmentation:

    def __init__(self, _img_path, _thres, _min_seg):

        # track iterations
        self.i = 0
        # track current region being processed
        self.current_region = 0

        # load image, get row and col counts
        self.img_path = _img_path
        self.img = self.read_img(_img_path)
        self.row, self.col = self.img.shape

        # set threshold
        self.thres = _thres
        self.min_seg = _min_seg

        # track which pixel has been looped
        self.looped = np.full((self.row,self.col), False, dtype=bool)
        # track which pixel has been grouped into a region
        self.region_map = np.full((self.row,self.col), -1, dtype=int)
        # track pixel counts, mean, and scatter for each region
        self.region_stats = []

    def read_img(self, _ima_path):
        grayscale_img = cv2.imread(_ima_path, cv2.IMREAD_GRAYSCALE)
        return grayscale_img

    def cal_hist(self, _img):
        hist = np.zeros(256, dtype=int)
        for i in range(self.row):
            for j in range(self.col):
                intensity = _img[i, j]
                hist[intensity] += 1
        return hist

    def gen_seed_pnts(self):
        hist = self.cal_hist(self.img)
        peaks = np.array([], dtype=int)
        seed_pnts = np.array([], dtype=int)
        std = np.std(hist)
        mean = np.mean(hist)
        img = np.array(self.img)

        for i in range(hist.size):
            if abs(hist[i] - mean) > std:
                historic_i = i - 5 if i > 4 else 0
                local_mean = np.mean(hist[historic_i: i])
                if hist[i] > local_mean:
                    peaks = np.append(peaks, i)

        for j in range(peaks.size):
            pixels = np.argwhere(img == peaks[j])
            rand_pix = np.random.randint(pixels.shape[0])
            seed_pnts = np.append(seed_pnts, pixels[rand_pix])
        seed_pnts = np.reshape(seed_pnts, (peaks.size, 2))

        peaks_vals = np.array([hist[x] for x in peaks])
        argsort_peak_vals = np.argsort(peaks_vals)
        seed_pnts = seed_pnts[argsort_peak_vals[::-1]]

        return seed_pnts

    def region_growing(self):
        seed_pnts = self.gen_seed_pnts()
        self.loop_seed_points(seed_pnts)
        # self.loop_unid_points()
        self.clean_up_small_segments()
        self.show_segmentation()

    def loop_seed_points(self, _seed_pnts):

        while _seed_pnts.size is not 0:

            sp_y = _seed_pnts[0, 0]
            sp_x = _seed_pnts[0, 1]

            # start a new region
            self.region_stats.append({
                'id': self.current_region,
                'member': [(sp_y, sp_x)],
                'mean': self.img[sp_y, sp_x],
                'scatter': 0.0001,
                'centroid': [sp_y, sp_x],
            })

            # add pixel as member of current region
            self.region_map[sp_y, sp_x] = self.current_region

            to_loop = np.array([_seed_pnts[0]])

            while to_loop.size is not 0:

                # set center pixel location
                y0 = to_loop[0][0]
                x0 = to_loop[0][1]

                # loop 8 neighbors
                neighbours = self.get_neighbours(y0, x0)
                for y, x in neighbours:
                    if self.region_map[y, x] < 0:

                        N = len(self.region_stats[self.current_region]['member'])
                        current_mean = self.region_stats[self.current_region]['mean']
                        current_scatter = self.region_stats[self.current_region]['scatter']
                        intensity_yx = self.img[y, x]

                        # calculate distribution T
                        partial_s_squared = (intensity_yx - current_mean)**2
                        T = math.sqrt((((N - 1) * N) / (N + 1)) * (((partial_s_squared) / (current_scatter))))

                        print(f'T = {T}')

                        if T <= self.thres:

                            # add pixel as member of current region
                            self.region_map[y, x] = self.current_region
                            self.region_stats[self.current_region]['id'] = self.current_region
                            self.region_stats[self.current_region]['member'].append((y, x))

                            # update mean
                            new_mean = ((N * current_mean) + intensity_yx) / (N + 1)
                            self.region_stats[self.current_region]['mean'] = new_mean

                            # update centroid
                            current_centroid_y = self.region_stats[self.current_region]['centroid'][0]
                            current_centroid_x = self.region_stats[self.current_region]['centroid'][1]
                            new_centroid_y = ((N * current_centroid_y) + y) / (N + 1)
                            new_centroid_x = ((N * current_centroid_x) + x) / (N + 1)
                            self.region_stats[self.current_region]['centroid'] = [new_centroid_y, new_centroid_x]

                            # update scatter
                            self.region_stats[self.current_region]['scatter'] += \
                                partial_s_squared + (N * (new_mean - current_mean) ** 2)

                            # add pixel to loop
                            to_loop = np.append(to_loop, [[y, x]], axis=0)

                        else:

                            # start a new region
                            _seed_pnts = np.append(_seed_pnts, [[y, x]], axis=0)

                # removing a point after it has been looped
                to_loop = np.delete(to_loop, 0, axis=0)

            self.current_region += 1

            # set to looped after looping through its neighbors
            self.looped[_seed_pnts[0][0], _seed_pnts[0][1]] = True

            # removing a seed point after its region has been defined
            _seed_pnts = np.delete(_seed_pnts, 0, axis=0)

            self.i += 1


    def loop_unid_points(self):
        no_id = (self.region_map < 0)
        not_looped = self.looped is False
        overlap = (no_id | not_looped)
        to_loop = np.array(np.where(overlap))
        while to_loop.size is not 0:
            y0 = to_loop[0][0]
            x0 = to_loop[1][0]
            to_loop = np.delete(to_loop, 0, axis=1)

    def clean_up_small_segments(self):

        img_area_thres = int((self.row * self.col) / self.min_seg)

        region_stats_cp = self.region_stats.copy()
        cp_i = 0

        for i in range(len(self.region_stats)):

            # check if region is too small and need to be grouped
            if len(region_stats_cp[cp_i]['member']) < img_area_thres:

                centroid_y = region_stats_cp[cp_i]['centroid'][0]
                centroid_x = region_stats_cp[cp_i]['centroid'][1]
                mean = region_stats_cp[cp_i]['mean']

                spatial_distance = []
                intensity_distance = []

                for j in range(len(region_stats_cp)):

                    # calculate spatial distance between centroids
                    j_centroid_y = region_stats_cp[j]['centroid'][0]
                    j_centroid_x = region_stats_cp[j]['centroid'][1]
                    spatial_distance.append(
                        math.sqrt(((j_centroid_y - centroid_y)**2)
                                  + ((j_centroid_x - centroid_x)**2))
                    )

                    # calculate intensity distance between regions
                    j_mean = region_stats_cp[j]['mean']
                    intensity_distance.append(
                        math.sqrt((j_mean - mean)**2)
                    )

                # finding nearest neighbors for both conditions
                order_intensity = np.array(intensity_distance).argsort()
                order_spatial = np.array(spatial_distance).argsort()

                # creating an empty array with size like order arrays
                scores = np.zeros(len(region_stats_cp), dtype=float)

                # select the nearest neighbor with the closest mean intensity
                for k in range(len(region_stats_cp)):
                    scores[order_intensity[k]] += k
                    scores[order_spatial[k]] += k

                # find minimum score
                sorted_score = scores.argsort()

                # group current region with winning region
                # get index of the most suitable region for merging
                m = sorted_score[1]

                # append members
                region_stats_cp[m]['member'] += region_stats_cp[cp_i]['member']

                # update centroid
                m_centroid_y = region_stats_cp[m]['centroid'][0]
                m_centroid_x = region_stats_cp[m]['centroid'][1]
                region_stats_cp[m]['centroid'][0] = (m_centroid_y + centroid_y) / 2.0
                region_stats_cp[m]['centroid'][1] = (m_centroid_x + centroid_x) / 2.0

                # update mean
                m_mean = region_stats_cp[m]['mean']
                region_stats_cp[m]['mean'] = (m_mean + mean) / 2.0

                # update scatter
                new_mean = region_stats_cp[m]['mean']
                N = len(region_stats_cp[m]['member'])
                new_term = ((mean - new_mean)**2) + (N * (new_mean - m_mean)**2)
                region_stats_cp[m]['scatter'] += new_term

                # update region map with id of most suitable region
                m_id = region_stats_cp[m]['id']
                for y, x in region_stats_cp[cp_i]['member']:
                    self.region_map[y, x] = m_id

                # delete merged region
                del region_stats_cp[cp_i]
                cp_i -= 1

            cp_i += 1

        self.region_stats = region_stats_cp

    def show_segmentation(self):
        bg_img = cv2.cvtColor(self.read_img(self.img_path), cv2.COLOR_GRAY2RGB)
        new_img = cv2.cvtColor(self.read_img(self.img_path), cv2.COLOR_GRAY2RGB)
        unique_regions = np.unique(self.region_map)

        color = [(255, 0, 0), (0, 255, 0), (0, 0, 255), (255, 255, 0), (0, 255, 255), (255, 0, 255), (128, 128, 0), (0, 128, 128), (128, 0, 128), (128, 0, 0), (0, 0, 128), (35, 35, 35), (128, 128, 128), (215, 215, 215)]
        dict = {}
        for i in range(len(unique_regions)):
            dict[unique_regions[i]] = i

        for i in range(self.row):
            for j in range(self.col):
                if self.region_map[i, j] >= 0:
                    new_img[i, j] = color[dict[self.region_map[i, j]]]
        final_img = cv2.addWeighted(bg_img, 0.7, new_img, 0.5, 0.0)
        cv2.imshow('Final Segments', final_img)
        cv2.waitKey(0)


    def in_bound(self, _y, _x):
        return 0 <= _y < self.row and 0 <= _x < self.col

    def terminate(self):
        return self.i > 200000 or np.count_nonzero(self.looped > 0) == self.row * self.col

    def get_neighbours(self, _y0, _x0):
        neighbours = []
        for i in (-1, 0, 1):
            for j in (-1, 0, 1):
                if (i, j) == (0, 0):
                    continue
                y = _y0 + i
                x = _x0 + j
                if self.in_bound(y, x):
                    neighbours.append((y, x))
        return neighbours

# 3.7, 30
Seg = Segmentation('image/daisy_112x112.png', 2.5, 30)
Seg.region_growing()