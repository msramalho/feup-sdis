package src.localStorage;

public class StoredChunk {
    String fileId; //file fileId sent in the backup request
    String chunkNumber;
    String path;
    public int repliesToPutchunk;

    public StoredChunk(String fileId, String chunkNumber) {
        this.fileId = fileId;
        this.chunkNumber = chunkNumber;
        repliesToPutchunk = 0;
        path = null;
    }

    // saves the chunk into the local file system
    public void saveChunk(){
        repliesToPutchunk++;
    	//...
    	//this.path = "/.../chunk"
    }

    // public String readChunkFromMemory(){
        //ler ficheiro do path e devolver o conteúdo
    // }
}
