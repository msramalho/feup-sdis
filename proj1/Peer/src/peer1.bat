echo Starting Peer...
javac Peer.java
::Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>
java Peer 1.0 1 8999 224.0.0.0 9000 224.0.0.1 9001 224.0.0.2 9002
::pause
echo --------DONE