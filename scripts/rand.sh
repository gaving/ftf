#!/bin/bash

if [ "$#" -gt 0 ]; then
    if [ -e $1 ]; then
        FILE=$1;
    else
        FILE="ids.txt"
    fi
else
    FILE="ids.txt"
fi

N=$(wc -l < "$FILE")
R=$((RANDOM % N + 1))
ID=$(sed -n "$R{p;q;}" "$FILE")

#echo "Trying $ID"
#echo "_____"
./run-console.sh $ID
