#!/bin/bash

. classpath.sh

cd ../src

javac \
-cp $CLASSPATH \
-Xlint:deprecation \
-Xlint:unchecked \
net/brokentrain/ftf/*.java \
net/brokentrain/ftf/core/*.java \
net/brokentrain/ftf/core/services/*.java \
net/brokentrain/ftf/core/lookup/*.java \
net/brokentrain/ftf/apps/*.java \
net/brokentrain/ftf/ui/web/*.java \
net/brokentrain/ftf/ui/gui/*.java \
net/brokentrain/ftf/ui/console/*.java
