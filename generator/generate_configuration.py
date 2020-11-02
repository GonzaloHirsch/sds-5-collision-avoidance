from numpy import random
import math
import argparse
import random as rnd

# Files
STATIC_FILE = "./parsable_files/static.txt"
DYNAMIC_FILE = "./parsable_files/dynamic.txt"

# Indexes
R = 0
M = 1
X = 2
Y = 3
VX = 4
VY = 5

def generate_people(width, height, people_count, people_radius, people_velocity, border_limit, main_radius, main_comfort_radius, mass):
    people_data = []

    people_data.append([main_radius, mass, main_comfort_radius, height / 2, 0, 0])

    # Determine limits to be used for X and Y
    x_left, x_right = border_limit, width - border_limit
    # Generating possible x_positions
    y_bottom, y_top = border_limit, height - border_limit

    people_generated = 0
    while (people_generated < people_count):
        # Generate X and Y values
        target_x = random.uniform(x_left, x_right)
        target_y = random.uniform(y_bottom, y_top)

        # Check if person is not overlapping others
        if not is_overlapping(people_data, target_x, target_y, people_radius, main_radius * 2):
            rnd_value = random.uniform(0, 1)
            if rnd_value > 0.5:
                vel = -1 * people_velocity
            else:
                vel = people_velocity
            people_data.append([people_radius, mass, target_x, target_y, 0, vel])
            people_generated += 1

    return people_data

def is_overlapping(people_data, x, y, radius, main_diameter):
    for p in people_data:
        distance = math.sqrt((p[X] - x)**2) - p[R] - radius - main_diameter
        if (distance <= 0):
            return True
    return False

def generate_static_file(filename, people_data, width, height, comfort_radius, wall_distance, pref_speed, pref_time, max_speed):
    f = open(filename, 'w')

    # Adding the width and height of area
    f.write('{} {}\n'.format(width, height))

    # Adding the comfort radius and the wall safe distance
    f.write('{} {}\n'.format(comfort_radius, wall_distance))

    # Adding speed info
    f.write('{} {} {}\n'.format(pref_speed, pref_time, max_speed))

    for data in people_data:
        f.write('{} {}\n'.format(data[R], data[M]))

    f.close()

def generate_dynamic_file(filename, people_data):
    f = open(filename, 'w')

    # We provide only the dynamic configuration at time 0
    f.write('0\n')

    # Adding the randomly generated
    for data in people_data:
        f.write('{} {} {} {}\n'.format(data[X], data[Y], data[VX], data[VY]))

    f.close()

# Generates both the dynamic and the static file
def generate_files(people_data, width, height, comfort_radius, wall_distance, pref_speed, pref_time, max_speed):
    generate_static_file(STATIC_FILE, people_data, width, height, comfort_radius, wall_distance, pref_speed, pref_time, max_speed)
    generate_dynamic_file(DYNAMIC_FILE, people_data)

# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Calculation of initial configuration")

    # add arguments
    parser.add_argument('-W', dest='area_width', required=True)
    parser.add_argument('-H', dest='area_height', required=True)
    parser.add_argument('-p', dest='people_count', required=True)
    parser.add_argument('-pr', dest='people_radius', required=True)
    parser.add_argument('-pv', dest='people_velocity', required=True)
    parser.add_argument('-cr', dest='comfort_radius', required=True)
    parser.add_argument('-wr', dest='wall_radius', required=True)
    parser.add_argument('-r', dest='main_radius', required=True)
    parser.add_argument('-m', dest='people_mass', required=True)
    parser.add_argument('-bl', dest='border_limit', required=True)
    parser.add_argument('-ps', dest='pref_speed', required=True)
    parser.add_argument('-pt', dest='pref_time', required=True)
    parser.add_argument('-ms', dest='max_speed', required=True)
    args = parser.parse_args()

    print("Generating people information...")
    people = generate_people(
        float(args.area_width),
        float(args.area_height),
        float(args.people_count),
        float(args.people_radius),
        float(args.people_velocity),
        float(args.border_limit),
        float(args.main_radius),
        float(args.comfort_radius),
        float(args.people_mass))
    generate_files(
        people,
        float(args.area_width),
        float(args.area_height),
        float(args.comfort_radius),
        float(args.wall_radius),
        float(args.pref_speed),
        float(args.pref_time),
        float(args.max_speed))

# call main
if __name__ == '__main__':
    main()