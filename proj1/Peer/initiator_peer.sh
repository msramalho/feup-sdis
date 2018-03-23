#!/usr/bin/env bash
clear
mkdir bin

javac -cp .:gson-2.8.2.jar:./bin/:./ -d bin src/*/*.java
echo "Source code compiled"

# get filename or use default
FILENAME="$1"
FILENAME=${FILENAME:-"file.txt"}  # If variable not set, use default.

#Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>
java -cp .:gson-2.8.2.jar:./bin/ src.main.Peer 1.0 2 8499 224.0.0.0 9000 224.0.0.1 9001 224.0.0.2 9002 $FILENAME 3