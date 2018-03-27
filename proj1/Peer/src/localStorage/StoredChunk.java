package src.localStorage;

import java.io.Serializable;

public class StoredChunk implements Serializable {
    String fileId; //file fileId sent in the backup request
    int chunkNumber;
    String path;
    public int repliesToPutchunk;

    public StoredChunk(String fileId, int chunkNumber) {
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
