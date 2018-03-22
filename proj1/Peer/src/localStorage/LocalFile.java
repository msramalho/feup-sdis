package localStorage;

import main.BackupChunkWorker;
import main.PeerConfig;

import java.util.ArrayList;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class LocalFile {
    static Integer CHUNK_SIZE = 64000;
    String id;
    String filename; // relative filename in the current file system
    ArrayList<LocalChunk> chunks;
    Integer replicationDegree; //desired replication degree
    PeerConfig peerConfig;

    public LocalFile(String id, String filename, Integer replicationDegree, PeerConfig peerConfig) {
        this.id = id;
        this.filename = filename;
        this.chunks = new ArrayList<LocalChunk>();
        this.replicationDegree = replicationDegree;
        this.peerConfig = peerConfig;
    }

    public void splitFile() {
        int totalBytesRead = 0;

        System.out.println("[LocalFile]file: " + filename);

        File file = new File(this.filename);
        int file_size = (int) file.length();
        System.out.println("[LocalFile][File Received length: " + file_size + "]");

        InputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println("[LocalFile]unable to read file: " + this.filename);
            e.printStackTrace();
        }
        int i = 0;
        byte[] temporaryChunk;
        while (totalBytesRead < file_size) {
            temporaryChunk = new byte[LocalFile.CHUNK_SIZE]; //Temporary Byte Array
            int bytesRead = 0;
            try {
                bytesRead = inStream.read(temporaryChunk, 0, LocalFile.CHUNK_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("[LocalFile]Chunk[" + i + "]" + " size: " + bytesRead);

            LocalChunk chunck1 = new LocalChunk(1, i, 0);
            chunks.add(chunck1);

            BackupChunkWorker bcWorker = new BackupChunkWorker(peerConfig, temporaryChunk, "00001", i, this.replicationDegree);
            this.peerConfig.threadPool.submit(bcWorker);
            i++;

            totalBytesRead += LocalFile.CHUNK_SIZE;
        }


        System.out.println("Size array: " + chunks.size());

    }
}
