#!/bin/bash

JAVA_PROGRAM_DIR=""
JAR_NAME="ftf.jar"

MSG0="Executing FTF..."
MSG1="Initialising FTF..."
MSG2="Java exec found in "
MSG3="Your java version is too old "
MSG4="You need to upgrade to JRE 1.5.x or newer from http://java.sun.com"
MSG5="Suitable java version found "
MSG6="Configuring environment..."
MSG7="You don't seem to have a valid JRE "
MSG8="Unable to locate java binary in "
MSG9=" hierarchy"
MSG10="Java exec not found in PATH, starting auto-search..."
MSG11="Java exec found in PATH. Verifying..."

function look_for_java() {
  JAVADIR=/usr/java
  IFS=$'\n'
  potential_java_dirs=(`ls -1 "$JAVADIR" | sort | tac`)
  IFS=
  for D in "${potential_java_dirs[@]}"; do
    if [[ -d "$JAVADIR/$D" && -x "$JAVADIR/$D/bin/java" ]]; then
      JAVA_PROGRAM_DIR="$JAVADIR/$D/bin/"
      echo $MSG2 $JAVA_PROGRAM_DIR
      if check_version ; then
        return 0
      else
        return 1
      fi
    fi
  done
  echo $MSG8 "${JAVADIR}/" $MSG9 ; echo $MSG4
  return 1
}

function check_version() {
  JAVA_HEADER=`${JAVA_PROGRAM_DIR}java -version 2>&1 | head -n 1`
  JAVA_IMPL=`echo ${JAVA_HEADER} | cut -f1 -d' '`
  if [ "$JAVA_IMPL" = "java" ] ; then
    VERSION=`echo ${JAVA_HEADER} | sed "s/java version \"\(.*\)\"/\1/"`
    if echo $VERSION | grep "^1.[0-4]" ; then
      echo $MSG3 "[${JAVA_PROGRAM_DIR}java = ${VERSION}]" ; echo $MSG4
      return 1
    else
      echo $MSG5 "[${JAVA_PROGRAM_DIR}java = ${VERSION}]" ; echo $MSG6
      return 0
    fi
  else
    echo $MSG7 "[${JAVA_PROGRAM_DIR}java = ${JAVA_IMPL}]" ; echo $MSG4
    return 1
  fi
}

echo $MSG1

# locate and test the java executable
if [ "$JAVA_PROGRAM_DIR" == "" ]; then
  if ! command -v java &>/dev/null; then
    echo $MSG10
    if ! look_for_java ; then
      exit 1
    fi
  else
    echo $MSG11
    if ! check_version ; then
      if ! look_for_java ; then
        exit 1
      fi
    fi
  fi
fi

# Attempt to find mozilla support
if [ "$MOZILLA_FIVE_HOME x" = " x" ] ; then

    LOCATIONS=(`echo "/usr/lib*/mozilla /usr/local/mozilla /opt/mozilla /usr/lib*/firefox /usr/local/firefox /opt/firefox /usr/lib*/MozillaFirebird /usr/local/MozillaFirebird /opt/MozillaFirebird /usr/lib*/xulrunner-* /usr/lib*/mozilla-* /usr/local/lib*/mozilla-*"`);

    for LOCATION in ${LOCATIONS[@]} ; do
        if [ -f ${LOCATION}/components/libwidget_gtk2.so ] ; then
            echo "Using mozilla support found at ${LOCATION}!"
            export MOZILLA_FIVE_HOME=${LOCATION};
            break
        fi
    done
else
    export MOZILLA_FIVE_HOME;
    echo "Could not locate mozilla or firefox, internal browser support will be disabled!"
fi

# Add the path to mozilla to our library path
if [ "$LD_LIBRARY_PATH x" = " x" ] ; then
    export LD_LIBRARY_PATH=${MOZILLA_FIVE_HOME};
else
    export LD_LIBRARY_PATH=${MOZILLA_FIVE_HOME}:${LD_LIBRARY_PATH}
fi

echo $MSG0

${JAVA_PROGRAM_DIR}java \
-Xms16m \
-Xmx128m \
-jar ${JAR_NAME}
"$@"
