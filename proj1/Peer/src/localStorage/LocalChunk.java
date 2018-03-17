package localStorage;

public class LocalChunk {
    Integer chunkNumber; // chunk ID
    Integer countAcks; // count the number of ACKs received for this chunk
    byte[] data;

    public LocalChunk(Integer chunkNumber, Integer countAcks, byte[] data) {
        this.chunkNumber = chunkNumber;
        this.countAcks = countAcks;
        this.data = data;
    }

//    public Json toJson(){
//
//    }
}
