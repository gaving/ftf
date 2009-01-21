#!/bin/bash

# generic build file for GNU/Linux

CLASSPATH=".:share/"
JAVA_PROGRAM_DIR=""
JAR_NAME="ftf.jar"
INCLUDE_DIR="include"
ENTRY_CLASS="net.brokentrain.ftf.apps.PDFDownloader"

MSG0="Loading Locator:"
MSG1="Starting Locator..."
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

# get the app dir if not already defined
if [ -z "$PROGRAM_DIR" ]; then
    PROGRAM_DIR=`dirname "$0"`
    PROGRAM_DIR=`cd "$PROGRAM_DIR"; pwd`
else
    if [ "$(echo ${PROGRAM_DIR}/*.jar)" = "${PROGRAM_DIR}/*.jar" ]; then
        echo "You seem to have set an invalid PROGRAM_DIR, unable to continue!"
        exit 1
    fi
fi

if ! (echo ${PROGRAM_DIR}/*.jar | grep $JAR_NAME >/dev/null 2>&1 ); then
    echo "Unable to locate $JAR_NAME in $PROGRAM_DIR, aborting!"
    exit 1
fi

CLASSPATH="$CLASSPATH:$PROGRAM_DIR/$JAR_NAME"

# build the classpath
for FILE in ${PROGRAM_DIR}/${INCLUDE_DIR}/*.jar; do
   CLASSPATH="${CLASSPATH:+${CLASSPATH}:}$FILE"
done

echo $MSG0

cd ${PROGRAM_DIR}

echo "${JAVA_PROGRAM_DIR}java \
-Xms16m \
-Xmx128m \
-cp \"${CLASSPATH}\" \
-Djava.library.path=\"${PROGRAM_DIR}\" \
$ENTRY_CLASS \
'$@'"

${JAVA_PROGRAM_DIR}java \
-Xms16m \
-Xmx128m \
-cp "${CLASSPATH}" \
-Djava.library.path="${PROGRAM_DIR}" \
$ENTRY_CLASS \
"$@"

echo "Fetcher terminated."
