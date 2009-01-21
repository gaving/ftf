#!/bin/bash

. classpath.sh

cd ../src

PROGRAM_NAME=$(basename $0)
PACKAGE_NAME="$1"
OUTPUT_DIR="../docs/diagrams"

show_invalid_usage() {
        echo "$PROGRAM_NAME: too few arguments
        Try '$PROGRAM_NAME <package name>'"
}

if [ ! $# -ge 1 ]; then
        show_invalid_usage
        exit
fi

javadoc -docletpath $UMLGRAPH_PATH -doclet \
gr.spinellis.umlgraph.doclet.UmlGraph \
-output tmp.dot \
-d $OUTPUT_DIR \
-all \
-qualify \
-nodefillcolor LemonChiffon \
-postfixpackage \
-useimports \
$PACKAGE_NAME && \
    dot -Tpng -o $OUTPUT_DIR/${PACKAGE_NAME}.png $OUTPUT_DIR/tmp.dot -Elabelfontcolor=DarkSlateBlue && \
    rm $OUTPUT_DIR/tmp.dot && \
    eog $OUTPUT_DIR/${PACKAGE_NAME}.png
