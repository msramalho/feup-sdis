package src.localStorage;

public class LocalChunk extends Chunk {

    public LocalChunk(String fileId, int chunkNo, int replicationDegree, byte[] chunk) {
        super(fileId, chunkNo, replicationDegree, chunk);
    }

}
