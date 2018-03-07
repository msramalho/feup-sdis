@echo off
prompt -------
@echo on
mkdir bin
javac -cp .;gson-2.8.2.jar;./bin/;./ -d bin src/main/*.java src/localStorage/*.java src/util/*.java
::Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>
java -cp .;gson-2.8.2.jar;./bin/ main.Peer 1.0 1 8499 224.0.0.0 9000 224.0.0.1 9001 224.0.0.2 9002 penguin.jpg 3
::pause