package src.util;

import java.util.Objects;

/**
 * parses a packet string into a queriable object
 */
public class Message {
    public String action;
    private String protocolVersion;
    private int senderId;
    public String fileId;
    public int chunkNo;
    public int replicationDegree;
    public String body;

    public Message(String action, String fileId, int chunkNo) {
        this.action = action;
        this.fileId = fileId;
        this.chunkNo = chunkNo;
    }

    public Message(byte[] packetMessageBytes) {
        String packetMessage = new String(packetMessageBytes).trim();
        try {
            this.parseMessage(packetMessage);
        } catch (Exception e) {
            System.err.println(String.format("[Message] - not a valid message (%d bytes): %s from PeerConfig, ignoring", packetMessage.length(), packetMessage));
        }
    }

    //<MessageType> <Version> <SenderId> <FileId> [<ChunkNo> <ReplicationDeg>] <CRLF><CRLF>[<Body>]
    private void parseMessage(String packetMessage) {
        String[] parts = packetMessage.split("\r\n\r\n", 2); // split only once
        String[] args = parts[0].split(" ");
        this.action = args[0];
        this.protocolVersion = args[1];
        this.senderId = Integer.parseInt(args[2]);
        this.fileId = args[3];
        this.chunkNo = (args.length >= 5) ? Integer.parseInt(args[4]) : -1;//save chunkNo if it exists
        this.replicationDegree = (args.length >= 6) ? Integer.parseInt(args[5]) : -1;//save replicationDegree if it exists
        // this.head = Arrays.copyOfRange(args, 4, args.length); // head keeps the optional args

        if (parts.length == 2) this.body = parts[1].substring(1); //save body if it exists
    }

    public String getAction() {
        return action;
    }

    public boolean isOwnMessage(int selfId) {
        return this.senderId == selfId;
    }

    public boolean isBackup() {
        return this.getAction().equals("BACKUP");
    }

    /**
     * comapare two messages -> used for queue.contains(...)
     * To be the same, they must have the same action, the same fileId and (if present) the same chunkNo
     *
     * @param o the object to comapre this to
     * @return true if messages match
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(action, message.action) &&
                fileId == message.fileId &&
                (chunkNo == message.chunkNo || chunkNo == -1);
    }

}
