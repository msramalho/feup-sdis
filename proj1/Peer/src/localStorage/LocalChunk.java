package src.localStorage;

public class LocalChunk {
	Integer fileId;
    Integer chunkNumber; // chunk ID
    Integer countAcks; // count the number of ACKs received for this chunk

    public LocalChunk(Integer fileId, Integer chunkNumber, Integer countAcks) {
        this.chunkNumber = chunkNumber;
        this.countAcks = countAcks;
        this.fileId = fileId;
    }

//    public Json toJson(){
//
//    }
}
