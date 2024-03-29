package src.util;

import java.net.DatagramPacket;
import java.util.Arrays;

/**
 * parses a packet string into a queryable object
 */
public class Message {
    public String action;
    public String protocolVersion;
    public int senderId;
    public String fileId;
    public int chunkNo;
    public int replicationDegree;
    public byte[] body = new byte[0];

    public Message(DatagramPacket packet) {
        try {
            this.parseMessage(packet);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(String.format("[Message] - not a valid message (%d bytes)...ignoring", packet.getData()));
        }
    }

    //<MessageType> <Version> <SenderId> <FileId> [<ChunkNo> <ReplicationDeg>] <CRLF><CRLF>[<Body>]
    private void parseMessage(DatagramPacket packet) {
        String packetMessage = new String(packet.getData());
        packetMessage = packetMessage.substring(0, Math.min(packet.getLength(), packetMessage.length()));
        String[] parts = packetMessage.split("\r\n\r\n", 2); // split only once
        int headerBytes = parts[0].length();
        parts[0] = parts[0].replaceAll("^ +| +$|( )+", "$1").trim(); //the message may have more than one space between field, this cleans it
        String[] args = parts[0].split(" ");
        this.action = args[0];
        this.protocolVersion = args[1];
        this.senderId = Integer.parseInt(args[2]);
        this.fileId = args[3];

        this.chunkNo = (args.length >= 5) ? Integer.parseInt(args[4]) : -1;//save chunkNo if it exists
        this.replicationDegree = (args.length >= 6) ? Integer.parseInt(args[5]) : -1;//save replicationDegree if it exists

        // retrieve the bytes received that belong to the body
        if (parts.length == 2) this.body = Arrays.copyOfRange(packet.getData(), headerBytes + 4, packet.getLength());//save body if it exists (64kB chunks)
    }

    public boolean isOwnMessage(int selfId) {
        return this.senderId == selfId;
    }

    public boolean isPutchunk() { return this.action.equals("PUTCHUNK"); }

    public boolean isGetChunk() { return this.action.equals("GETCHUNK"); }

    public boolean isStored() { return this.action.equals("STORED"); }

    public boolean isChunk() { return this.action.equals("CHUNK"); }

    public boolean isDelete() { return this.action.equals("DELETE"); }

    public boolean isRemoved() { return this.action.equals("REMOVED"); }

    public boolean isAdele() { return this.action.equals("ADELE"); }

    public boolean isDeleted() { return this.action.equals("DELETED");}

    public static byte[] createMessage(String header) { return Message.createMessage(header, new byte[0]); }

    public static byte[] createMessage(String header, byte[] body) {
        byte[] head = header.getBytes();
        byte[] combined = new byte[head.length + body.length];

        System.arraycopy(head, 0, combined, 0, head.length);
        System.arraycopy(body, 0, combined, head.length, body.length);
        return combined;
    }

    @Override
    public String toString() {
        return "Message{" +
                "action='" + action + '\'' +
                ", protocolVersion='" + protocolVersion + '\'' +
                ", senderId=" + senderId +
                ", fileId='" + fileId + '\'' +
                ", chunkNo=" + chunkNo +
                ", replicationDegree=" + replicationDegree +
                '}';
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
        return action.equals(message.action) &&
                fileId.equals(message.fileId) &&
                (chunkNo == message.chunkNo || chunkNo == -1);
    }

}
