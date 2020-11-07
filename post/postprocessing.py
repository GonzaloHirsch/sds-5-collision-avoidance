import numpy as np
import math
import argparse
import random as rnd
import statistics
import matplotlib.pyplot as plt
from matplotlib.ticker import (MultipleLocator, FormatStrFormatter,
                               AutoMinorLocator)

INPUT_FILE = "./parsable_files/output.txt"
STATS_FILE = "./parsable_files/stats.txt"
STATIC_FILE = "./parsable_files/static.txt"

TIME_TRAVELLED = 't'
DISTANCE_TRAVELLED = 'd'
MEAN_VELOCITY = 'v'
COLLISION_NUMBER = 'c'
PLOT_TIME_TRAVELLED = 'pt'
PLOT_DISTANCE_TRAVELLED = 'pd'
PLOT_MEAN_VELOCITY = 'pv'
PLOT_COLLISION_NUMBER = 'pc'

EVASIVE_PARTICLE_INDEX = 0
X_VALUE = 0
Y_VALUE = 1
HEURISTIC_LINE = 2
HEURISTIC_INDEX = 3
RADIUS_LINE = 3
RADIUS_INDEX = 0
ERROR = "Invalid type error"

MEAN = 'mean'
STDEV = 'stdev'

# Given the data of the simulation
def extract_results(filename):
    # Extracting the positions and velocities

    f = open(filename, 'r')
    positions = {}
    velocities = {}
    times = []

    particle_index = 0

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            times.append(float(data[0]))
            particle_index = 0
        else:
            data = [float(x) for x in data]

            # Checking if the indexes exist
            if not particle_index in positions:
                positions[particle_index] = []
            if not particle_index in velocities:
                velocities[particle_index] = []

            positions[particle_index].append([data[0], data[1]])
            velocities[particle_index].append([data[2], data[3]])
            particle_index += 1

    return times, positions, velocities

def parse_heuristic(filename):
    f = open(filename, 'r')

    index = 0
    heuristic = 0

    for line in f:
        if index == HEURISTIC_LINE:
            data = line.rstrip("\n").split(" ")
            heuristic = float(data[HEURISTIC_INDEX])

        index += 1

    f.close()

    return heuristic

def parse_radius(filename):
    f = open(filename, 'r')

    index = 0
    radius = {}

    for line in f:
        if index >= RADIUS_LINE:
            data = line.rstrip("\n").split(" ")
            radius[index - RADIUS_LINE] = float(data[RADIUS_INDEX])
        index += 1

    f.close()

    return radius

def distance_travelled(positions):

    x = np.array([point[X_VALUE] for point in positions[EVASIVE_PARTICLE_INDEX]])
    y = np.array([point[Y_VALUE] for point in positions[EVASIVE_PARTICLE_INDEX]])

    dx = x[1:]-x[:-1]
    dy = y[1:]-y[:-1]

    step_size = np.sqrt(dx**2+dy**2)

    cumulative_distance = np.concatenate(([0], np.cumsum(step_size)))

    final_idx = len(cumulative_distance) - 1

    print("The distance travelled by the particle was " + str(cumulative_distance[final_idx]))

    return cumulative_distance[final_idx]


def elapsed_time(times):
    final_idx = len(times) - 1
    print("The time taken to reach the goal was " + str(times[final_idx]))
    return times[final_idx]


def mean_velocity(times, distance):
    final_idx = len(times) - 1
    average_velocity = distance / times[final_idx]
    print("The mean velocity of the particle was " + str(average_velocity))
    return average_velocity

def number_of_collisions(times, positions, radius):
    count = 0

    # Preparing collisions map
    collisions = {}
    for i in range(len(radius)):
        collisions[i] = 0

    for i in range(len(times)):
        # Current position of main particle
        curr = positions[0][i]
        for j in range(1, len(radius)):
            has_col = check_if_colliding(curr, positions[j][i], radius[0], radius[j])
            if has_col:
                if collisions[j] == 0:
                    count += 1
                collisions[j] += 1
            else:
                collisions[j] = 0

    print("The number of collisions of the particle was " + str(count))
    return count

def check_if_colliding(a_pos, b_pos, a_radius, b_radius):
    dist = math.sqrt((a_pos[0] - b_pos[0])**2 + (a_pos[1] - b_pos[1])**2) - a_radius - b_radius
    if dist <= 0:
        return True
    return False

def save_results(p, time, distance, velocity, collisions):
    wf = open(STATS_FILE, 'a')
    wf.write('{}\n'.format(p))
    wf.write('{} {}\n'.format(TIME_TRAVELLED, time))
    wf.write('{} {}\n'.format(DISTANCE_TRAVELLED, distance))
    wf.write('{} {}\n'.format(MEAN_VELOCITY, velocity))
    wf.write('{} {}\n'.format(COLLISION_NUMBER, collisions))

def calculate_mean(values):
    return statistics.mean(values)

def calculate_stdev(values, mean):
    if len(values) > 1:
        return statistics.stdev(values, mean)
    else:
        return 0


def calculate_mean_and_std(filename):
    stats = {TIME_TRAVELLED: {}, DISTANCE_TRAVELLED: {}, MEAN_VELOCITY: {}, COLLISION_NUMBER: {}}
    heuristic = 0

    f = open(filename, "r")
    for line in f:
        data = line.rstrip("\n").split(" ")

        if len(data) == 1:
            heuristic = float(data[0]) #TODO -> define
            if not heuristic in stats[TIME_TRAVELLED]:
                stats[TIME_TRAVELLED][heuristic] = []
                stats[DISTANCE_TRAVELLED][heuristic] = []
                stats[MEAN_VELOCITY][heuristic] = []
                stats[COLLISION_NUMBER][heuristic] = []
        else:
            # Retrieving the values
            type = data[0]
            value = float(data[1])

            stats[type][heuristic].append(value)

    f.close()

    for type in stats:
        for heuristic in stats[type]:
            mean = calculate_mean(stats[type][heuristic])
            stdev = calculate_stdev(stats[type][heuristic], mean)
            stats[type][heuristic] = {MEAN: mean, STDEV: stdev}

    return stats


def organize_data_type(data):
    heuristics = []
    means = []
    stdevs = []

    for heuristic in data:
        heuristics.append(heuristic)
        means.append(data[heuristic][MEAN])
        stdevs.append(data[heuristic][STDEV])

    heuristics, means, stdevs = zip(*sorted(zip(heuristics, means, stdevs)))
    return heuristics, means, stdevs


def plot_graph(x_values, means, std, type):

    # Set the x axis label
    # TODO: Ver si es tiempo de anticipacion o DMAX
    plt.xlabel('Tiempo de Anticipación [s]')

    switcher = {
        TIME_TRAVELLED: "Tiempo transcurrido [s]",
        DISTANCE_TRAVELLED: "Distancia recorrida [m]",
        MEAN_VELOCITY: "Velocidad promedia [m/s]",
        COLLISION_NUMBER: "Cantidad de colisiones"
    }
    y_label = switcher.get(type, ERROR)

    if y_label == ERROR:
        print ('Invalid type of graph')
        return

    plt.ylabel(y_label)

    plt.errorbar(x_values, means, yerr=std, fmt='o', color='black', ecolor='lightgray', elinewidth=3, capsize=0)
    plt.gca().xaxis.set_minor_locator(MultipleLocator(0.25))
    plt.show()


# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Post processing for the points data to generate data statistics")

    # add arguments
    parser.add_argument('-p', dest='plot_type', required=False)
    parser.add_argument('-s', action='store_true')
    args = parser.parse_args()

    if args.s:
        # Result extraction
        p = parse_heuristic(STATIC_FILE)
        radius = parse_radius(STATIC_FILE)
        times, positions, velocities = extract_results(INPUT_FILE)

        # Metrics calculations
        time_travel = elapsed_time(times)
        distance_travel = distance_travelled(positions)
        average_velocity = mean_velocity(times, distance_travel)
        collisions = number_of_collisions(times, positions, radius)

        #Saving metrics
        save_results(p, time_travel, distance_travel, average_velocity, collisions)

    if args.plot_type:
        if args.plot_type == PLOT_TIME_TRAVELLED:
            stats = calculate_mean_and_std(STATS_FILE)
            p, times, stdevs = organize_data_type(stats[TIME_TRAVELLED])
            plot_graph(p, times, stdevs, TIME_TRAVELLED)

        elif args.plot_type == PLOT_DISTANCE_TRAVELLED:
            stats = calculate_mean_and_std(STATS_FILE)
            p, distances, stdevs = organize_data_type(stats[DISTANCE_TRAVELLED])
            plot_graph(p, distances, stdevs, DISTANCE_TRAVELLED)

        elif args.plot_type == PLOT_MEAN_VELOCITY:
            stats = calculate_mean_and_std(STATS_FILE)
            p, velocities, stdevs = organize_data_type(stats[MEAN_VELOCITY])
            plot_graph(p, velocities, stdevs, MEAN_VELOCITY)

        elif args.plot_type == PLOT_COLLISION_NUMBER:
            stats = calculate_mean_and_std(STATS_FILE)
            p, velocities, stdevs = organize_data_type(stats[COLLISION_NUMBER])
            plot_graph(p, velocities, stdevs, COLLISION_NUMBER)


# call main
if __name__ == '__main__':
    main()