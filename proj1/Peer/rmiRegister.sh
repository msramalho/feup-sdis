#!/usr/bin/env bash
clear
mkdir bin

javac -cp .:./bin/:./ -d bin src/*/*.java
echo "rmi Started..."

#Usage:
rmiregistry -J-Djava.rmi.server.codebase=file:/src/main/Peer