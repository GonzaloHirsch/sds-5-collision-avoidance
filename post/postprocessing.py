import numpy as np
import math
import argparse
import random as rnd
import statistics
import matplotlib.pyplot as plt

INPUT_FILE = "./parsable_files/output.txt"

TIME_TRAVELLED = 't'
DISTANCE_TRAVELLED = 'd'
MEAN_VELOCITY = 'v'

EVASIVE_PARTICLE_INDEX = 0
X_VALUE = 0
Y_VALUE = 1

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


def distance_travelled(times, positions):

    x = np.array([point[X_VALUE] for point in positions[EVASIVE_PARTICLE_INDEX]])
    y = np.array([point[Y_VALUE] for point in positions[EVASIVE_PARTICLE_INDEX]])

    dx = x[1:]-x[:-1]
    dy = y[1:]-y[:-1]

    step_size = np.sqrt(dx**2+dy**2)

    cumulative_distance = np.concatenate(([0], np.cumsum(step_size)))

    final_idx = len(cumulative_distance) - 1

    print("The distance travelled by the particle was " + str(cumulative_distance[final_idx]))

    # Plotting the graph of distance travelled vs time
    plt.gca().set_xlabel("Delta de Tiempo [s]")
    plt.gca().set_ylabel("Distancia Recorrida [m]")
    plt.plot(times, cumulative_distance)

    plt.show()


def elapsed_time(times):
    final_idx = len(times) - 1
    print("The time taken to reach the goal was " + str(times[final_idx]))


def mean_velocity(velocities):
    vx = np.array([velocity[X_VALUE] for velocity in velocities[EVASIVE_PARTICLE_INDEX]])
    vy = np.array([velocity[Y_VALUE] for velocity in velocities[EVASIVE_PARTICLE_INDEX]])

    v_module = np.sqrt(vx**2+vy**2)

    cumulative_velocity = np.sum(v_module)
    average_velocity = cumulative_velocity / len(velocities[EVASIVE_PARTICLE_INDEX])

    print("The mean velocity of the particle was " + str(average_velocity))


# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Post processing for the points data to generate data statistics")

    # add arguments
    parser.add_argument('-p', dest='process_type', required=True)
    args = parser.parse_args()

    times, positions, velocities = extract_results(INPUT_FILE)

    if args.process_type == TIME_TRAVELLED:
        elapsed_time(times)
    elif args.process_type == DISTANCE_TRAVELLED:
        distance_travelled(times, positions)
    elif args.process_type == MEAN_VELOCITY:
        mean_velocity(velocities)

# call main
if __name__ == '__main__':
    main()
