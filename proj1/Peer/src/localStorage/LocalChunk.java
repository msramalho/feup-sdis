package localStorage;

public class LocalChunk {
    Integer chunkNumber;
    Integer countAcks; // count the number of ACKs received for this chunk

    public LocalChunk(Integer chunkNumber, Integer countAcks) {
        this.chunkNumber = chunkNumber;
        this.countAcks = countAcks;
    }

//    public Json toJson(){
//
//    }
}
