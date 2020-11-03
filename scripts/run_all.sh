#!/bin/bash

radius_base=0.6
radius_mult=0.2

#!/bin/bash
for i in {0..7}
do
  comfort=$(echo "$i * $radius_mult + $radius_base" | bc)
  echo "Radius $comfort"
  for j in {0..50}
  do
    echo "Run $j"
    python3 generator/generate_configuration.py -W 36 -H 9 -p 10 -pr 0.5 -pv 1.3 -cr $comfort -r 0.5 -bl 2 -m 70 -wr 0.6 -ps 1.3 -pt 0.5 -ms 2
    java -jar ./target/sds-tp5-1.0-jar-with-dependencies.jar -sf ./parsable_files/static.txt -df ./parsable_files/dynamic.txt -dt 0.001 -dt2 0.01
    if [[ $? -eq 0 ]]; then
        python3 post/postprocessing.py -s
    fi
  done
done
