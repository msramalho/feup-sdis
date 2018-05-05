# SDIS 2016/2017: Project 2 -- Distributed Backup Service w/ Peer Clustering

SDIS 2016/2017 - 2nd Semester
Project 2 -- Distributed Backup Service w/ Peer Clustering

[Official script](https://web.fe.up.pt/~pfs/aulas/sd2018/projs/proj1/proj1.html)

### The script files
 * `peer.sh` starts a peer and can start the RMI(operation which must be done in the beginning): `<peerId:default=1> <resetDatabase:default=false> <startRMI:default=false> <protocolVersion:default=1.0>`
 * `testApp.sh` communicates with a Peer through RMI and sends the action desired: `<fileName:default=file.txt> <peerId:default=1> <action:default=1=BACKUP> <replicationDegree:default=2|spaceReclaim:default=2KBytes>`

## How to test
* Clone the repository root folder (`Peer`) into your local file system
* (The compilation and execution has been placed on the bash files described above (`peer.sh` and `testApp.sh`))
* Open 4 terminals [TERMINAL_1, TERMINAL_2, TERMINAL_3, TERMINAL_4] inside the `Peer` folder
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

#### NOTE
If you append an `e` to the testApph.sh action, this will instead call the enhanced version of the subprotocol, if it exists, or the default if it doesn't: * `sh testApp.sh largePenguin.jpg 1 2e` - on peer `1`, start action `2e` (RESTOREENH) of file `largePenguin.jpg` (uses TCP btw)


# Protocols

## Create new Cluster
When a new cluster is created it needs to query on the global McControl for available ClusterIds and then map the id into a Multicast address, which must be between:  `224.0.0.0` and `239.255.255.255`.

The Peer forming a new Cluster will send a `CLUSTERING` message and every cluster must send a `CLUSTER <ClusterId>` for their cluster.

If a Peer from cluster X sends that cluster's address then all the other Peers in the cluster will be prevented from doing so for that `CLUSTERING` message. 

The new peer will use the first available `Id` and map into a Multicast Adress by incrementing `Id` from `224.0.0.0`


## Try to join a Cluster
When a Peer loggs into the network it will send a `HELLO <PeerId> <LEVEL>` message. This means this peer has no Cluster for level `<LEVEL>` (initially it is 0). Every cluster in that LEVEL which has an available slot shall respond with `AVAILABLE <PeerId> <ClusterId>` and stop should it receive the sam message from any other. The Peer will then join that Cluster. The available message shall specify which Peer it is available to to avoid errors.

## Higher Level Clusters
All peers know the 3 default/global Multicast groups.

When a Peer initiates:
 * Send a `JOIN <LEVEL>` to the global McControl with `LEVEL = 0`
     * If a Peer answers (there is a slot available in its cluster) all others are silenced. The new Peer and the responding Peer exchange information about the upper Clusters so that the new peer can listen on all important channels.
     * If no Peer answers, then this Peer will [create its own cluster]()





