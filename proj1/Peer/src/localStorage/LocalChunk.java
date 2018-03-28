package src.localStorage;

import java.io.Serializable;
import java.util.HashSet;

public class LocalChunk extends Chunk implements Serializable {

    public LocalChunk(String fileId, int chunkNo, int replicationDegree, byte[] chunk) {
        super(fileId, chunkNo, replicationDegree, chunk);
    }

}
