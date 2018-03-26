#!/usr/bin/env bash

mkdir bin
clear

javac -cp .:./bin/:./ -d bin src/*/*.java
echo "Source code compiled"

#Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>
java -cp .:./bin/ src.main.Peer 1.0 2 8499 224.0.0.0 9000 224.0.0.1 9001 224.0.0.2 9002

