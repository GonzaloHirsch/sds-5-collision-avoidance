#!/bin/bash

time_base=2.5
time_mult=0.25

#!/bin/bash
for i in {0..10}
do
  time_limit=$(echo "$i * $time_mult + $time_base" | bc)
  echo "Time limit $time_limit"
  for j in {0..25}
  do
    echo "Run $j"
    python3 generator/generate_configuration.py -W 36 -H 9 -p 10 -pr 0.5 -pv 1.3 -cr 0.9 -r 0.5 -bl 2 -m 70 -wr 0.6 -ps 1.3 -pt 0.5 -ms 2 -at $time_limit
    java -jar ./target/sds-tp5-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -dt 0.001 -dt2 0.01
    if [[ $? -eq 0 ]]; then
        python3 post/postprocessing.py -s
    fi
  done
done
