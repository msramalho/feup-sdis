#!/usr/bin/env bash

mkdir bin
clear

javac -cp .:./bin/:./ -d bin src/*/*.java
echo "Source code compiled"

# get peerID or use default
PEERID="$1"
PEERID=${PEERID:-"2"}  # If variable not set, use default.

# if the second argument equals 1 then delete the database
if [ "$2" = "1" ]
then
    echo Deleting internal_state_peer_$PEERID.
    rm -rf internal_state_peer_$PEERID
fi

#Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>
java -cp .:./bin/ src.main.Peer 1.0 $PEERID 8499 224.0.0.0 9000 224.0.0.1 9001 224.0.0.2 9002

