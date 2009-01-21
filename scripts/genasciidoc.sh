#!/bin/bash

. classpath.sh

cd ../src

PROGRAM_NAME=$(basename $0)
PACKAGE_NAME="$1"
OUTPUT_DIR="../docs/asciidoc"

for i in $OUTPUT_DIR/*.txt; do
    python $ASCIIDOC_PATH/asciidoc.py -a icons $i
done
