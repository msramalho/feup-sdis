echo Starting Peer...
::javac -cp gson-2.8.2.jar Peer.java *.java
::javac -cp .:gson-2.8.2.jar Peer.java PeerConfig.java InternalState.java LocalChunk.java LocalFile.java StoredChunk.java
javac Peer.java
::Usage: <protocolVersion> <peerId> <serviceAccessPoint> <mccIP> <mccPort> <mdbIp> <mdbPort> <mdrIp> <mdrPort>
java -cp .:gson-2.8.2.jar Peer 1.0 1 8999 224.0.0.0 9000 224.0.0.1 9001 224.0.0.2 9002
::pause
echo --------DONE