package src.util;

import src.localStorage.InternalState;
import src.localStorage.LocalFile;

import static java.lang.Integer.min;
import static java.lang.Math.max;

/**
 * parses a packet string into a queryable object
 */
public class Message {
    public String action;
    private String protocolVersion;
    public int senderId;
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
            e.printStackTrace();
            System.err.println(String.format("[Message] - not a valid message (%d bytes): %s...ignoring", packetMessage.length(), packetMessage));
        }
    }

    //<MessageType> <Version> <SenderId> <FileId> [<ChunkNo> <ReplicationDeg>] <CRLF><CRLF>[<Body>]
    private void parseMessage(String packetMessage) {
        String[] parts = packetMessage.split("\r\n\r\n", 2); // split only once
        parts[0] = parts[0].replaceAll("^ +| +$|( )+", "$1"); //the message may have more than one space between field, this cleans it
        String[] args = parts[0].split(" ");
        this.action = args[0];
        this.protocolVersion = args[1];
        this.senderId = Integer.parseInt(args[2]);
        this.fileId = args[3];

        this.chunkNo = (args.length >= 5) ? Integer.parseInt(args[4]) : -1;//save chunkNo if it exists
        this.replicationDegree = (args.length >= 6) ? Integer.parseInt(args[5]) : -1;//save replicationDegree if it exists

        if (parts.length == 2) this.body = parts[1].substring(1, min(parts[1].length(), LocalFile.CHUNK_SIZE + 1)); //save body if it exists (64kB chunks)
    }

    public boolean isOwnMessage(int selfId) {
        return this.senderId == selfId;
    }

    public boolean isPutchunk() { return this.action.equals("PUTCHUNK"); }

    public boolean isGetchunk() { return this.action.equals("GETCHUNK"); }

    public boolean isStored() { return this.equals("STORED"); }

    public boolean needsDispacher(InternalState is) {
        // is Putchunk -> needsDispacher
        // is Stored and is not about one of my files -> needsDispacher
        // is Stored and is about one of my files -> goes to queue for BackUpChunk
        //TODO: make chunk for my requests add the CHUNK message to the queue and not to the dispatcher (return false here)
        //TODO: make GETCHUNK call the dispacher (return true here)
        return isPutchunk() || (isStored() && !is.isLocalFile(fileId)) ; //|| isGetChunk() || !(isChunk() && is.isLocalFile(fileId));
    }

    public byte[] getBodyBytes() {
        return body.getBytes();
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
