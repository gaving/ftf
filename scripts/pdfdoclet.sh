#!/bin/sh

# Simple example shell script which demonstrates
# how to use the PDFDoclet with javadoc directly
# (which means: without ANT).

DOCLET=com.tarsec.javadoc.pdfdoclet.PDFDoclet
DOCLETPATH="/mnt/media/utilities/pdfdoclet-binary-1.0.2/jar/pdfdoclet-1.0.2-all.jar"
PACKAGES="net.brokentrain.ftf"

javadoc -doclet $DOCLET -docletpath $DOCLETPATH -pdf ../docs/pdf/javadoc.pdf -config \
javadoc.properties -private -sourcepath ../src/ $PACKAGES
