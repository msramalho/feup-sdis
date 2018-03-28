package src.localStorage;

import src.util.Message;

import java.io.Serializable;
import java.util.HashSet;

public abstract class Chunk implements Serializable {
    public String fileId; //file fileId sent in the backup request
    public int chunkNo;
    public int replicationDegree;
    public HashSet<Integer> peersAcks; // a set of the IDs of Peers that have saved this chunk
    public transient byte[] chunk; // the chunk bytes for this chunk

    public Chunk() { chunk = null;}

    public Chunk(Message m) {
        this(m.fileId, m.chunkNo, m.replicationDegree, m.getBodyBytes());
    }

    public Chunk(String fileId, int chunkNo, int replicationDegree, byte[] chunk) {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.chunk = chunk;
        this.replicationDegree = replicationDegree;
        peersAcks = new HashSet<>();
    }

    public void addAck(Integer peerId) {
        peersAcks.add(peerId);
    }

    public int countAcks() {return peersAcks.size();}

    public String getUniqueId() {
        return StoredChunk.getUniqueId(fileId, chunkNo);
    }

    public static String getUniqueId(String fileId, int chunkNo) {
        return fileId + "_" + chunkNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chunk that = (Chunk) o;
        return chunkNo == that.chunkNo && fileId.equals(that.fileId);
    }
}
