package src.localStorage;

import src.util.Logger;
import src.util.Message;

import javax.json.JsonObject;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public abstract class Chunk implements Serializable {
    public String fileId = null; //file fileId sent in the backup request
    public JsonObject fileMetadata;
    public int chunkNo = -1;
    public int replicationDegree = 0;
    public static long TIME_TO_LIVE = 3600;
    public boolean deleted = false;
    public Date expirationDate;
    public HashSet<Integer> peersAcks = new HashSet<>(); // a set of the IDs of Peers that have saved this chunk
    public transient byte[] chunk = null; // the chunk bytes for this chunk
    public boolean gotAnswer = false; // true if the current peer saw a CHUNK message while sleeping
    transient Logger logger = new Logger(this);

    public Chunk() {}

    public Chunk(Message m) {
        this(m.fileId, m.fileMetadata, m.chunkNo, m.replicationDegree, m.body);
    }

    public Chunk(String fileId, int chunkNo) {this(fileId, null, chunkNo, 0, null);}

    public Chunk(String fileId, JsonObject fileMetadata, int chunkNo, int replicationDegree, byte[] chunk) {
        this.fileId = fileId;
        this.chunkNo = chunkNo;
        this.chunk = chunk;
        this.replicationDegree = replicationDegree;
        this.expirationDate = getDefaultExpirationdate();
        this.fileMetadata = fileMetadata;
    }

    private Date getDefaultExpirationdate() {
        LocalDateTime localDateTime = LocalDateTime.now();
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        Date now = Date.from(instant);

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        return new Date(cal.getTimeInMillis() + TIME_TO_LIVE);
    };

    public void addAck(Integer peerId) { peersAcks.add(peerId); }

    public int countAcks() {return peersAcks.size();}

    public String getUniqueId() { return StoredChunk.getUniqueId(fileId, chunkNo); }

    static String getUniqueId(String fileId, int chunkNo) { return fileId + "_" + chunkNo; }

    public String getShortId() { return fileId.substring(0, 10) + "_" + chunkNo; }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public String getFileMetadata() { return fileMetadata.toString(); };

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chunk that = (Chunk) o;
        return chunkNo == that.chunkNo && fileId.equals(that.fileId);
    }


    @Override
    public String toString() {
        return "Chunk{" +
                "fileId='" + fileId.substring(0, 10) + '\'' +
                ", chunkNo=" + chunkNo +
                ", deleted=" + deleted +
                ", replicationDegree=" + replicationDegree +
                ", peersAcks= (" + peersAcks.size() + ")" + peersAcks +
                '}';
    }
}