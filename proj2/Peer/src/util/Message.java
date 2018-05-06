package src.util;

import java.net.DatagramPacket;
import java.util.AbstractMap;
import java.util.Arrays;

/**
 * parses a packet string into a queryable object
 */
public class Message {
    public String action;
    public String protocolVersion;
    public int senderId;
    public String fileId;
    public int level = -1;
    public int clusterId = -1;
    public int receiverId;
    public int chunkNo;
    public int replicationDegree;
    public byte[] body = new byte[0];
    private Logger logger = new Logger(this);

    public Message(DatagramPacket packet) {
        try {
            this.parseMessage(packet);
        } catch (Exception e) {
            e.printStackTrace();
            logger.err(String.format("not a valid message (%d bytes)...ignoring", packet.getData().length));
        }
    }

    //

    /**
     * <MessageType> <Version> <SenderId> <FileId> [<ChunkNo> <ReplicationDeg>] <CRLF><CRLF>[<Body>]
     * or
     * <MessageType> <Version> <SenderId> [<Level>[:<ClusterId>] <receiverId>] <CRLF><CRLF>[<Body>]
     */
    private void parseMessage(DatagramPacket packet) {
        String packetMessage = new String(packet.getData()); // byte[] -> String
        packetMessage = packetMessage.substring(0, Math.min(packet.getLength(), packetMessage.length())); // trim
        String[] parts = packetMessage.split("\r\n\r\n", 2); // split only once
        int headerBytes = parts[0].length();
        parts[0] = parts[0].replaceAll("^ +| +$|( )+", "$1").trim(); //the message may have more than one space between field, this cleans it

        // process header
        String[] args = parts[0].split(" ");
        this.action = args[0];
        this.protocolVersion = args[1];
        this.senderId = Integer.parseInt(args[2]);

        // the difference between the two types of messages is that fileId is a string and level is not, but if level is string it is because of level:clusterId
        if (args.length >= 4 && (Utils.isInt(args[3]) || args[3].contains(":"))) { // cluster message
            if (args[3].contains(":")) {
                String[] clusterParts = args[3].split(":"); // <Level>:<ClusterId>
                this.level = Integer.parseInt(clusterParts[0]);
                this.clusterId = Integer.parseInt(clusterParts[1]);
            } else {
                this.level = Integer.parseInt(args[3]); // save the cluster level
            }
            this.receiverId = (args.length >= 5) ? Integer.parseInt(args[4]) : -1; // save receiverId if it exists
        } else { // service message
            this.fileId = (args.length >= 4) ? args[3] : ""; // save file id if it exists
            this.chunkNo = (args.length >= 5) ? Integer.parseInt(args[4]) : -1;//save chunkNo if it exists
            this.replicationDegree = (args.length >= 6) ? Integer.parseInt(args[5]) : -1;//save replicationDegree if it exists
        }


        // retrieve the bytes received that belong to the body
        if (parts.length == 2) this.body = Arrays.copyOfRange(packet.getData(), headerBytes + 4, packet.getLength());//save body if it exists (64kB chunks)
    }

    boolean isOwnMessage(int selfId) { return this.senderId == selfId; }

    public boolean isPutchunk() { return this.action.equals("PUTCHUNK"); }

    public AbstractMap.SimpleEntry<String, Integer> getTCPCoordinates() {
        //parse the body of the message, which should contain IP:Port of the TCP socket on the other Peer
        String[] parts = new String(body).split(":");
        if (parts.length != 2) {
            logger.err(String.format("Expected IP:Port but got: %s", new String(body)));
            System.exit(1);
        }
        return new AbstractMap.SimpleEntry<>(parts[0], Integer.parseInt(parts[1]));
    }


    public static byte[] create(String header, Object... options) { return Message.create(header, new byte[0], options); }

    public static byte[] create(String header, byte[] body, Object... options) { return Message.create(String.format(header, options), body); }

    public static byte[] create(String header) { return Message.create(header, new byte[0]); }

    public static byte[] create(String header, byte[] body) {
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

    public String getBodyStr() { return new String(body); }
}
