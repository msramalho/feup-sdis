package src.localStorage;

import java.io.Serializable;
import java.util.HashSet;

public class LocalChunk implements Serializable {
    public transient LocalFile file;
    public transient byte[] chunk;

    public Integer chunkNo; // chunk ID
    // public Integer countAcks; // count the number of ACKs received for this chunk
    public HashSet<Integer> peersAcks; //list of the ids of the peers that stored this chunk

    public LocalChunk(LocalFile file, Integer chunkNo, byte[] chunk) {
        this.chunkNo = chunkNo;
        this.file = file;
        this.chunk = chunk;
        this.peersAcks = new HashSet<>();
    }

    public void addPeerAck(Integer peerId) {
        peersAcks.add(peerId);
    }

    public int getCountAcks() {return peersAcks.size();}
}
