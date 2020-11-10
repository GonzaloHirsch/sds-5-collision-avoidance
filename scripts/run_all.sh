#!/bin/bash

time_base=0
time_mult=0.2

#!/bin/bash
for i in {0..5}
do
  time_limit=$(echo "$i * $time_mult + $time_base" | bc)
  echo "Time limit $time_limit"
  for j in {1..20}
  do
    echo "Run $j"
    python3 generator/generate_configuration.py -W 36 -H 9 -p 25 -pr 0.3 -pv 1.3 -cr 1.2 -r 0.3 -bl 1 -m 70 -wr 0.6 -ps 1.3 -pt 0.5 -ms 2 -dmin $time_limit
    java -jar ./target/sds-tp5-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -dt 0.001 -dt2 0.01
    if [[ $? -eq 0 ]]; then
        python3 post/postprocessing.py -s
    fi
  done
done