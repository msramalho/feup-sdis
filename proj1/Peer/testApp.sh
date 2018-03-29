#!/usr/bin/env bash
clear
mkdir bin

javac -cp .:./bin/:./ -d bin src/*/*.java
echo "Source code compiled"

# get filename or use default
FILENAME="$1"
FILENAME=${FILENAME:-"file.txt"}  # If variable not set, use default.

#Usage:
java -cp .:./bin/ src.client.TestApp 4 BACKUP $FILENAME 3