#!/bin/zsh

FILE="ids.txt"
N=$(wc -l < "$FILE")

for i in {1..$@[1]}; do
	R=$((RANDOM % N + 1))
	echo $(sed -n "$R{p;q;}" "$FILE")
done
