package src.localStorage;

import java.io.Serializable;

public class LocalChunk extends Chunk implements Serializable {
    public LocalChunk(String fileId, int chunkNo) {
        super(fileId, chunkNo);
    }

    public LocalChunk(String fileId, int chunkNo, Integer replicationDegree, byte[] chunk) {super(fileId, chunkNo, replicationDegree, chunk); }

}
