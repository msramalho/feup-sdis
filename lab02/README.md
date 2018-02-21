# Compilation
 * `javac Server.java` for server
 * `javac Client.java` for client
 
# Execution
#### Server
`java Server <srvc_port> <mcast_addr> <mcast_port>`, where:
 * `<srvc_port>` is the port number where the server provides the service
 * `<mcast_addr>` is the IP address of the multicast group used by the server to advertise its service.
 * `<mcast_port>` is the multicast group port number used by the server to advertise its service.
#### Client
`java client <mcast_addr> <mcast_port> <oper> <opnd> *`, where:
 * `<mcast_addr>` is the IP address of the multicast group used by the server to advertise its service;
 * `<mcast_port>` is the port number of the multicast group used by the server to advertise its service;
 * `<oper>` is ''register'' or ''lookup'', depending on the operation to invoke;
 * `<opnd> *` is the list of operands of the specified operation:
     * `<plate number> <owner name>`, for register;
     * `<plate number>`, for lookup.

Run client for each operation: `java Client <host_name> <port> <operation> <operation_arguments>`
   * `REGISTER XX-XX-11 Owner_name`
   * `LOOKUP XX-XX-11`
 
# Explanation

 1. Client joins broadcast group at (IP, PORT) = (`<mcast_addr>`, `<mcast_port>`) - (`<mcast_addr>` between 224.0.0.0 and 239.255.255.2525)
 2. Client waits for the server to bradcast its PORT (the server IP will be available from the `DatagramPacket.getAddress()`)
 3. Server eventually broadcasts the port (does not need to join the group)
 4. Server serves client (uses single thread due to lab specifications bt two threads would not need timeout)
