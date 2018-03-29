package src.localStorage;

import src.util.Message;

import java.io.Serializable;
import java.util.HashSet;

public abstract class Chunk implements Serializable {
    public String fileId = null; //file fileId sent in the backup request
    public int chunkNo = -1;
    public int replicationDegree = 0;
    public boolean deleted = false;
    public HashSet<Integer> peersAcks = new HashSet<>(); // a set of the IDs of Peers that have saved this chunk
    public transient byte[] chunk = null; // the chunk bytes for this chunk

    public Chunk() {}

    public Chunk(Message m) {
        this(m.fileId, m.chunkNo, m.replicationDegree, m.body);
    }

    public Chunk(String fileId, int chunkNo) {this(fileId, chunkNo, 0, null);}

    public Chunk(String fileId, int chunkNo, int replicationDegree, byte[] chunk) {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.chunk = chunk;
        this.replicationDegree = replicationDegree;
    }

    public void addAck(Integer peerId) { peersAcks.add(peerId); }

    public int countAcks() {return peersAcks.size();}

    public String getUniqueId() { return StoredChunk.getUniqueId(fileId, chunkNo); }

    public static String getUniqueId(String fileId, int chunkNo) { return fileId + "_" + chunkNo; }

    public String getShortId() {
        return fileId.substring(0, 10) + "_" + chunkNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chunk that = (Chunk) o;
        return chunkNo == that.chunkNo && fileId.equals(that.fileId);
    }


    @Override
    public String toString() {
        return "StoredChunk{" +
                "fileId='" + fileId.substring(0, 10) + '\'' +
                ", chunkNo=" + chunkNo +
                ", deleted=" + deleted +
                ", replicationDegree=" + replicationDegree +
                ", peersAcks= (" + peersAcks.size() + ")" + peersAcks +
                '}';
    }

}