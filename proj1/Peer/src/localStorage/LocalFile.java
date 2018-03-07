package localStorage;

import java.util.ArrayList;

public class LocalFile {
    String id;
    String filename; // path + filename in the current file system
    ArrayList<LocalChunk> chunks;
    Integer replicationDegree; //desired replication degree

    public LocalFile(String id, String filename, ArrayList<LocalChunk> chunks, Integer replicationDegree) {
        this.id = id;
        this.filename = filename;
        this.chunks = chunks;
        this.replicationDegree = replicationDegree;
    }
}
