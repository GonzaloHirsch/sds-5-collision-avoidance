package app;

import pca.Particle;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ConfigurationParser {
    public final static Map<Integer, Particle> particles = new HashMap<>();
    public static double safeWallDistance;
    public static double comfortRadius;
    public static double width;
    public static double height;
    public static double prefSpeed;
    public static double prefTime;
    public static double maxSpeed;
    public static double anticipationTime;

    /**
     * Parses the files given with the static and dynamic information in order to configure the initial state of GOL
     *
     * @param staticFileName  File path for the static file
     * @param dynamicFileName File path for the dynamic file
     */
    public static void ParseConfiguration(String staticFileName, String dynamicFileName) throws FileNotFoundException {
        ParseStaticData(staticFileName);
        ParseDynamicData(dynamicFileName);
    }

    private static void ParseStaticData(String staticFileName) throws FileNotFoundException {
        File file = new File(staticFileName);
        Scanner sc = new Scanner(file);

        width = sc.nextDouble();
        height = sc.nextDouble();

        comfortRadius = sc.nextDouble();
        safeWallDistance = sc.nextDouble();

        prefSpeed = sc.nextDouble();
        prefTime = sc.nextDouble();
        maxSpeed = sc.nextDouble();
        anticipationTime = sc.nextDouble();

        int particleCount = 0;

        while (sc.hasNext()){
            // Extracting data
            double radius = sc.nextDouble();
            double mass = sc.nextDouble();

            // Generating the new particle
            Particle p = new Particle(particleCount, radius, mass, comfortRadius, maxSpeed, prefSpeed, prefTime, anticipationTime);
            particles.put(particleCount, p);

            particleCount++;
        }
    }

    private static void ParseDynamicData(String dynamicFileName) throws FileNotFoundException {
        File file = new File(dynamicFileName);
        Scanner sc = new Scanner(file);

        // Skipping the time of the file which is 0
        sc.nextDouble();

        int particleCount = 0;

        while (sc.hasNext()){
            // Extracting data
            double x = sc.nextDouble();
            double y = sc.nextDouble();
            double vx = sc.nextDouble();
            double vy = sc.nextDouble();

            // Adding the positions and velocity to the particle
            Particle p = particles.get(particleCount);
            p.setPosition(x, y);
            p.setVelocity(vx, vy);

            particles.put(particleCount, p);

            particleCount++;
        }
    }
}
