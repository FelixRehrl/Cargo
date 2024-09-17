#!/bin/bash

JAVA_CMD="java -jar examples/Cargo/target/Cargo.jar" 
SEEDS=(42 43 44)  

ALGORITHMS=(
  "BFS"
  "DFS"
  "MINCOST"
  "A*:UNMET_GOALS"
  "BFG:UNMET_GOALS"
)

INPUT_FILES=(
  "first_instance.txt"
  "second_instance.txt"
  "third_instance.txt"
  "fourth_instance.txt"
  # "fifth_instance.txt"
  # "sixth_instance.txt"
  # "seventh_instance.txt"
)

OUTPUT_CSV="data/output_stats.csv"

RUN_WITH_SEEDS=true

for arg in "$@"; do
  case $arg in
    --no-seed)
      RUN_WITH_SEEDS=false
      shift
      ;;
    *)
      echo "Unknown option: $arg"
      echo "Usage: $0 [--no-seed]"
      exit 1
      ;;
  esac
done

if [ "$RUN_WITH_SEEDS" = true ]; then

  echo "Running experiments WITH multiple seeds..."
  echo "Seed,Algorithm,InputFile,Duration_sec" > $OUTPUT_CSV

  for seed in "${SEEDS[@]}"; do
    for input in "${INPUT_FILES[@]}"; do
      for algo in "${ALGORITHMS[@]}"; do
        echo "Running Algorithm: $algo on Input File: $input"
        $JAVA_CMD $MAIN_CLASS --algos="$algo" -f="$input" -v=0 -o=5 --seed="$seed"
      done
    done
  done

else

  echo "Running experiments WITHOUT multiple seeds..."
  echo "Seed,Algorithm,InputFile,Duration_sec" > $OUTPUT_CSV

  for input in "${INPUT_FILES[@]}"; do
    for algo in "${ALGORITHMS[@]}"; do

      echo "Running Algorithm: $algo | Input File: $input"

      $JAVA_CMD --algos="$algo" -f="$input" -v=0 -o=5
      
      
    done
  done

fi
  echo "All experiments completed. Results saved to $OUTPUT_CSV."
