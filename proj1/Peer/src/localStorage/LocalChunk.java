package src.localStorage;

public class LocalChunk extends Chunk {
    public LocalChunk(String fileId, int chunkNo) {
        super(fileId, chunkNo);
    }

    public LocalChunk(String fileId, int chunkNo, Integer replicationDegree, byte[] chunk) {super(fileId, chunkNo, replicationDegree, chunk); }

}
