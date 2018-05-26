#!/usr/bin/env bash
clear
mkdir bin

javac -classpath .:./bin/:./:./lib/javax.json-api-1.1.2.jar:./lib/javax.json-1.1.2.jar -d bin src/*/*.java

echo "Source code compiled"

# get filename or use default
FILENAME="$1"
FILENAME=${FILENAME:-"file.txt"} # If variable not set, use default.

# get peerID or use default
PEERID="$2"
PEERID=${PEERID:-"1"}

#get the action
ACTION="$3"
ACTION=${ACTION:-"1"}

#get the optional replicationDegree or spaceReclaim
OPT="$4"
OPT=${OPT:-"2"}

JAVAARGS="-classpath .:./bin/:./lib/javax.json-api-1.1.2.jar:./lib/javax.json-1.1.2.jar src.client.TestApp"

if [ $ACTION = "1" ]; then
    java $JAVAARGS $PEERID BACKUP $FILENAME $OPT
elif [ $ACTION = "1e" ]; then
    java $JAVAARGS $PEERID BACKUPENH $FILENAME $OPT
elif [ $ACTION = "2" ]; then
    java $JAVAARGS $PEERID RESTORE $FILENAME
elif [ $ACTION = "2e" ]; then
    java $JAVAARGS $PEERID RESTOREENH $FILENAME
elif [ $ACTION = "2m" ]; then
    CREATIONTIME="$4"
    LASTMODIFIEDTIME="$5"
    SIZE="$6"
    java $JAVAARGS $PEERID RESTOREMETADATA $FILENAME $CREATIONTIME $LASTMODIFIEDTIME $SIZE
elif [ $ACTION = "3" ]; then
    java $JAVAARGS $PEERID DELETE $FILENAME
elif [ $ACTION = "3e" ]; then
    java $JAVAARGS $PEERID DELETEENH $FILENAME
elif [ $ACTION = "4" ]; then
    java $JAVAARGS $PEERID RECLAIM $OPT
elif [ $ACTION = "4e" ]; then
    java $JAVAARGS $PEERID RECLAIMENH $OPT
elif [ $ACTION = "5" ]; then
    java $JAVAARGS $PEERID STATE
elif [ $ACTION = "e5" ]; then
    java $JAVAARGS $PEERID STATEENH
else
    echo Invalid action $ACTION must be 1-5
fi

#Usage: <fileName:default=file.txt> <peerId:default=1> <action:default=1=BACKUP> <replicationDegree|spaceReclaim:default=2>
