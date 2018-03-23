package src.localStorage;

import src.worker.BackupChunk;
import src.main.PeerConfig;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class LocalFile {
    transient PeerConfig peerConfig;
    static transient Integer CHUNK_SIZE = 64000;

    public String fileId;
    String filename; // relative filename in the current file system
    Integer replicationDegree; //desired replication degree

    public LocalFile(String fileId, String filename, Integer replicationDegree, PeerConfig peerConfig) {
        this.fileId = fileId;
        this.filename = filename;
        this.replicationDegree = replicationDegree;
        this.peerConfig = peerConfig;
    }

    public void splitFile() {
        System.out.println("[LocalFile] - splitting file: " + filename);

        // read from the filesystem into an input stream
        File file = new File(this.filename);
        int file_size = (int) file.length();
        InputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println("[LocalFile] - unable to read file: " + this.filename);
            e.printStackTrace();
        }

        // split file and add to worker thread
        int i = 0, totalBytesRead = 0;
        while (totalBytesRead < file_size) {
            byte[] temporaryChunk = new byte[LocalFile.CHUNK_SIZE]; //Temporary Byte Array
            try {
                inStream.read(temporaryChunk, 0, LocalFile.CHUNK_SIZE);
            } catch (IOException e) {
                e.printStackTrace();
            }

            BackupChunk bcWorker = new BackupChunk(peerConfig, new LocalChunk(this, i, temporaryChunk), this.replicationDegree);
            this.peerConfig.threadPool.submit(bcWorker);
            i++;

            totalBytesRead += LocalFile.CHUNK_SIZE;
        }
    }
}
