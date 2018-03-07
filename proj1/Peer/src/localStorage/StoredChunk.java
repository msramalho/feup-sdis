package localStorage;

public class StoredChunk {
    String fileId; //file id sent in the backup request
    String chunkNumber;

    public StoredChunk(String fileId, String chunkNumber) {
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
    }
}
