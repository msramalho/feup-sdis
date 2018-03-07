package util;

import java.util.Arrays;

/**
 * parses a packet string into a queriable object
 */
public class Message {
    private String action;
    String protocolVersion;
    int senderId;
    int fileId;
    String[] head;
    String body;

    public Message(byte[] packetMessageBytes) {
        String packetMessage = new String(packetMessageBytes);
        try {
            this.parseMessage(packetMessage);
        } catch (Exception e) {
            System.err.println(String.format("[Message] - not a valid message (%d bytes): %s from PeerConfig", packetMessage.length(), packetMessage));
            e.printStackTrace();
        }
    }

    //<MessageType> <Version> <SenderId> <FileId> [<ChunkNo> <ReplicationDeg>] <CRLF><CRLF>[<Body>]
    private void parseMessage(String packetMessage) {
        System.out.println(packetMessage);
        String[] parts = packetMessage.split("\r\n\r\n", 2); // split only once
        String[] args = parts[0].split(" ");
        this.action = args[0];
        this.protocolVersion = args[1];
        this.senderId = Integer.parseInt(args[2]);
        this.fileId = Integer.parseInt(args[3]);
        this.head = Arrays.copyOfRange(args, 4, args.length); // head keeps the optional args
        if (parts.length == 2) this.body = parts[1]; //save body if it exists
    }

    public String getAction() {
        return action;
    }

    public boolean isBackup() {
        return this.getAction().equals("BACKUP");
    }
}
