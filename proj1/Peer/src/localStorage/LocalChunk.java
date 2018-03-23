package src.localStorage;

public class LocalChunk {
    public transient String fileId;
    public Integer chunkNo; // chunk ID
    public Integer countAcks; // count the number of ACKs received for this chunk
    public transient byte[] chunk;

    public LocalChunk(String fileId, Integer chunkNo, byte[] chunk) {
        this.chunkNo = chunkNo;
        this.countAcks = 0
        this.fileId = fileId;
        this.chunk = chunk;
    }
}
