package src.localStorage;

public class LocalChunk {
    public transient LocalFile file;
    public Integer chunkNo; // chunk ID
    public Integer countAcks; // count the number of ACKs received for this chunk
    public transient byte[] chunk;

    public LocalChunk(LocalFile file, Integer chunkNo, byte[] chunk) {
        this.chunkNo = chunkNo;
        this.countAcks = 0;
        this.file = file;
        this.chunk = chunk;
    }
}
