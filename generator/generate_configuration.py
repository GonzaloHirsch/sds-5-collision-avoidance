from numpy import random
import math
import argparse
import random as rnd

# Files
STATIC_FILE = "./parsable_files/static.txt"
DYNAMIC_FILE = "./parsable_files/dynamic.txt"

# Indexes
R = 0
X = 1
Y = 2
VX = 3
VY = 4

def generate_people(width, height, people_count, people_radius, people_velocity, border_limit):
    people_data = []

    # Determine limits to be used for X and Y
    x_left, x_right = border_limit, width - border_limit
    # Generating possible x_positions
    available_x_pos = [x for x in range(int(x_left), int(x_right))]
    rnd.shuffle(available_x_pos)
    y_bottom, y_top = border_limit, height - border_limit

    people_generated = 0
    while (people_generated < people_count):
        # Generate X and Y values
        target_x = available_x_pos[people_generated]
        target_y = random.uniform(y_bottom, y_top)

        # Check if person is not overlapping others
        if not is_overlapping(people_data, target_x, target_y, people_radius):
            rnd_value = random.uniform(0, 1)
            if rnd_value > 0.5:
                vel = -1 * people_velocity
            else:
                vel = people_velocity
            people_data.append([people_radius, target_x, target_y, 0, vel])
            people_generated += 1

    return people_data

def is_overlapping(people_data, x, y, radius):
    for p in people_data:
        distance = math.sqrt((p[X] - x)**2 + (p[Y] - Y)**2) - p[R] - radius
        if (distance <= 0):
            return True
    return False

def generate_static_file(filename, people_data, width, height):
    f = open(filename, 'w')

    # Adding the width and height of area
    f.write('{} {}\n'.format(width, height))

    # Adding the number of people
    f.write('{}\n'.format(len(people_data)))

    for data in people_data:
        f.write('{}\n'.format(data[R]))

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
def generate_files(people_data, width, height):
    generate_static_file(STATIC_FILE, people_data, width, height)
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
    parser.add_argument('-r', dest='main_radius', required=True)
    parser.add_argument('-bl', dest='border_limit', required=True)
    args = parser.parse_args()

    print("Generating people information...")
    people = generate_people(float(args.area_width), float(args.area_height), float(args.people_count), float(args.people_radius), float(args.people_velocity), float(args.border_limit))
    generate_files(people, float(args.area_width), float(args.area_height))

# call main
if __name__ == '__main__':
    main()