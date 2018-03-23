package src.localStorage;

import src.worker.BackupChunk;
import src.main.PeerConfig;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class LocalFile {
    transient PeerConfig peerConfig;
    public static transient Integer CHUNK_SIZE = 64000;

    public String fileId;
    String filename; // relative filename in the current file system
    public Integer replicationDegree; //desired replication degree
    public ArrayList<LocalChunk> chunks; //the chunks in this file

    public LocalFile(String filename, Integer replicationDegree, PeerConfig peerConfig) {
        this.peerConfig = peerConfig;
        this.filename = filename;
        this.replicationDegree = replicationDegree;
        this.chunks = new ArrayList<>();
        loadFileId();
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
            LocalChunk localChunk = new LocalChunk(this, i, temporaryChunk);
            chunks.add(localChunk);
            BackupChunk bcWorker = new BackupChunk(peerConfig, localChunk);
            this.peerConfig.threadPool.submit(bcWorker);
            i++;

            totalBytesRead += LocalFile.CHUNK_SIZE;
        }
    }

    private void loadFileId() {
        //TODO: use file metadata and/or maybe content instead of just filename to generate the unique fileId/hash
        String hashSource = filename;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(hashSource.getBytes(StandardCharsets.UTF_8));
            fileId = String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
