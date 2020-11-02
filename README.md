# SdS - TP5

## Files
### static.txt
The static file contains the static information for the system, the structure is:
```
area_width area_height
comfort_radius wall_comfort_radius
pref_speed pref_time max_speed
main_agent_radius main_agent_mass
obstacle_1_radius obstacle_1_mass
.
.
.
obstacle_n_radius obstacle_n_mass
```

### dynamic.txt
The dynamic file contains the initial positions and velocities for all the particles, the structure is:
```
0
main_agent_x main_agent_y main_agent_vx main_agent_vy
obstacle_1_x obstacle_1_y obstacle_1_vx obstacle_1_vy
.
.
.
obstacle_n_x obstacle_n_y obstacle_n_vx obstacle_n_vy
```

The contents of each line have spaces in between.

### output.txt
The output file contains the positions and velocities for all objects in the simulation. It is the simulation output and the structure is:
```
0
main_agent_x main_agent_y main_agent_vx main_agent_vy
obstacle_1_x obstacle_1_y obstacle_1_vx obstacle_1_vy
.
.
.
obstacle_n_x obstacle_n_y obstacle_n_vx obstacle_n_vy
time_1
main_agent_x main_agent_y main_agent_vx main_agent_vy
obstacle_1_x obstacle_1_y obstacle_1_vx obstacle_1_vy
.
.
.
obstacle_n_x obstacle_n_y obstacle_n_vx obstacle_n_vy
...
time_m
main_agent_x main_agent_y main_agent_vx main_agent_vy
obstacle_1_x obstacle_1_y obstacle_1_vx obstacle_1_vy
.
.
.
obstacle_n_x obstacle_n_y obstacle_n_vx obstacle_n_vy
```

## Generation
To generate dynamic input, a python script is used:
```
python3 generator/generate_configuration.py -W 18 -H 9 -p 10 -pr 0.5 -pv 1.3 -cr 0.9 -r 0.5 -bl 2 -m 70 -wr 0.6 -ps 1.3 -pt 0.5 -ms 2
```

This will generate an area of 14x7 (width x height) with 10 people obstacles of 0.15m radius with Y velocity of 3.
Agents are generated in columns with a 1m of offset from the borders.
The comfort radius for the main person is 0.5m and it's radius 0.2m. 

## Simulation
To run the simulation run:
```
java -jar ./target/sds-tp5-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -dt 0.001 -dt2 0.01
```

## Statistics
To run save the results of the run to stats.txt:
```
python3 post/postprocessing.py -s
```
To get the mean and standard deviation graphs for time travelled, distance travelled and mean velocity respectively:
```
python3 post/postprocessing.py -p pt 
python3 post/postprocessing.py -p pd 
python3 post/postprocessing.py -p pv 

```

## Visualization
To postprocess for visualization run:
```
python3 ./visualization/process.py
```

## Useful Links
 - [Paper por Predictive Collision Avoidance](https://cdef6307-a-62cb3a1a-s-sites.googlegroups.com/site/ikaramouzas/publications/mig09.pdf?attachauth=ANoY7copv4Hrlavk7Rx1LDqp1gwuBjVaJUEiixbQPJZE09czwFXFSnKEbkHFB-Y-TQMRLd1Ou8AOEUWrfpF3kXWGDaos_YAclKakiLDT7E7HlYUMlSIh-2R51n87WBkjOHp3ne_JGyEdy4eLxNmJjCovH5qlXTd_IhYQZJ-5ZOk4F-FqgnhengDVFIsB36LklQW_HQzG8JI-lQWkO2yXmXS5QK7bY9GkQ_gkBu1jTlN1S5vW5HwwtCk%3D&attredirects=0)
 - [Site for Predictive Collision Avoidance papers](https://sites.google.com/site/ikaramouzas/pam)