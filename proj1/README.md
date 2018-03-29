# SDIS 2016/2017: Project 1 -- Distributed Backup Service

SDIS 2016/2017 - 2nd Semester  
Project 1 -- Distributed Backup Service  

[Official script](https://web.fe.up.pt/~pfs/aulas/sd2018/projs/proj1/proj1.html)

### The script files
 * `peer.sh` starts a peer and can start the RMI(operation which must be done in the beginning): `<peerId:default=1> <resetDatabase:default=false> <startRMI:default=false>`
 * `testApp.sh` communicates with a Peer through RMI and sends the action desired: `<fileName:default=file.txt> <peerId:default=1> <action:default=1=BACKUP> <replicationDegree:default=2>`

## Test Project:
* Open 4 terminals [TERMINAL_1, TERMINAL_2, TERMINAL_3, TERMINAL_4] on the `proj1/Peer` folder
* In **TERMINAL_1**: `sh peer.sh 1 0 1`. Start peer 1, do not reset database, and start RMI. This will open a new terminal that will start the `rmiregistry`. And will also load Peer ID 1.
* In **TERMINAL_2**: `sh peer.sh 2`. Start peer 2
* In **TERMINAL_3**: `sh peer.sh 3`. Start peer 3
* you can add as many peers as desired, this example uses only 3
* Use the `testApp` interface in one or more of the following actions in **TERMINAL_4**:
    * `sh testApp.sh largePenguin.jpg 1 1 1` - on peer `1`, start action `1` (BACKUP) of file `largePenguin.jpg` with replicationDegree of `2` (could ommit 2 as it is the default)
    * `sh testApp.sh largePenguin.jpg 1 2` - on peer `1`, start action `2` (RESTORE) of file `largePenguin.jpg`
    * `sh testApp.sh largePenguin.jpg 1 3` - on peer `1`, start action `3` (DELETE) of file `largePenguin.jpg`
    * `sh testApp.sh largePenguin.jpg 1 4 0` - on peer `1`, start action `4` (RECLAIM) with `0` KBytes
    * `sh testApp.sh largePenguin.jpg 1 5` - on peer `1`, start action `5` (STATE)
