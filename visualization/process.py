import argparse

# Files with the values
STATIC_FILE = "./parsable_files/static.txt"
INPUT_FILE = "./parsable_files/output.txt"
OUTPUT_FILE = "./parsable_files/animation.xyz"

COLOR_YELLOW = [235/255, 192/255, 52/255]
COLOR_RED = [199/255, 59/255, 44/255]

def generate_system_frames(filename, outfilename, radius, width, height):
    f = open(filename, 'r')

    # Extract data

    processed_data = {}
    times = []

    for line in f:
        data = line.rstrip("\n").split(" ")
        if len(data) == 1:
            time = float(data[0])
            processed_data[time] = []
            times.append(time)
        else:
            point = [float(x) for x in data]
            processed_data[time].append(point)

    # Generate animation file

    f = open(outfilename, 'w')

    n = len(processed_data[times[0]]) + 5

    for time in times:
        f.write('{}\n'.format(n))
        f.write('\n')
        point_index = 0

        data_for_time = processed_data[time]

        # Adding the particles
        for point in data_for_time:
            if point_index == 0:
                # Particle that avoids collision
                cl = COLOR_RED
            else:
                #Particles moving in the y direction
                cl = COLOR_YELLOW

            f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(radius[point_index], point[0], point[1], cl[0], cl[1], cl[2]))
            point_index += 1

        # Adding dummy particles
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, 0, 0, 0, 0, 0))
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, 0, height, 0, 0, 0))
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, width, height, 0, 0, 0))
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.00001, width, 0, 0, 0, 0))
        f.write('{}\t{}\t{}\t{}\t{}\t{}\n'.format(0.3, 34.65, 4.5, 0, 0, 1))

    f.close()

def parse_particle_radius(filename):
    f = open(filename, 'r')

    radius = []
    index = 0
    length = 0

    for line in f:
        data = line.rstrip("\n").split(" ")
        if index > 2:
            radius.append(float(data[0]))
        elif index == 0:
            width = float(data[0])
            height = float(data[1])
        index += 1

    return radius, width, height


# main() function
def main():
    # Command line args are in sys.argv[1], sys.argv[2] ..
    # sys.argv[0] is the script name itself and can be ignored
    # parse arguments
    parser = argparse.ArgumentParser(description="Post processing to generate animation frames")

    # add arguments
    # parser.add_argument('-t', dest='process_type', required=True)
    args = parser.parse_args()

    particle_radius, width, height = parse_particle_radius(STATIC_FILE)

    generate_system_frames(INPUT_FILE, OUTPUT_FILE, particle_radius, width, height)

# call main
if __name__ == '__main__':
    main()