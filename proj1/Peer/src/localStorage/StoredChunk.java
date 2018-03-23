package src.localStorage;

public class StoredChunk {
    String fileId; //file fileId sent in the backup request
    String chunkNumber;
    String path;

    public StoredChunk(String fileId, String chunkNumber) {
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
    }

    public void saveChunk(){
    	//...
    	//this.path = "/.../chunk"
    }
}
