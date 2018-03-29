package src.localStorage;

import src.util.Message;
import src.worker.BackupChunk;
import src.main.PeerConfig;
import src.worker.DeleteChunk;
import src.worker.RestoreChunk;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Integer.min;

public class LocalFile {
    PeerConfig peerConfig;
    public static Integer CHUNK_SIZE = 64000;

    public String fileId;
    String filename; // relative filename in the current file system
    public Integer replicationDegree; //desired replication degree
    ArrayList<LocalChunk> chunks;
    public int numChunks;

    public LocalFile(String filename, Integer replicationDegree, PeerConfig peerConfig) {
        this.peerConfig = peerConfig;
        this.filename = filename;
        this.replicationDegree = replicationDegree;
        loadFileId();
        File file = new File(this.filename);
        int file_size = (int) file.length();
        numChunks = (int) Math.ceil(file_size / CHUNK_SIZE) + 1;
    }

    public void splitFile() {
        System.out.println("[LocalFile] - splitting file: " + filename);

        // read from the filesystem into an input stream
        File file = new File(this.filename);
        int file_size = (int) file.length();
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("[LocalFile] - unable to read file: " + this.filename);
            e.printStackTrace();
        }


        // split file and add to worker thread
        int i = 0, totalBytesRead = 0;
        while (totalBytesRead < file_size) {
            int chunkSize = min(LocalFile.CHUNK_SIZE, (file_size - totalBytesRead));
            byte[] temporaryChunk = new byte[chunkSize]; //Temporary Byte Array
            try {
                totalBytesRead += inStream.read(temporaryChunk, 0, chunkSize);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LocalChunk localChunk = new LocalChunk(fileId, i, replicationDegree, temporaryChunk);
            BackupChunk bcWorker = new BackupChunk(peerConfig, localChunk, true);
            this.peerConfig.threadPool.submit(bcWorker);
            i++;
        }
        try {
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reconstructFile() throws IOException, ExecutionException, InterruptedException {
        System.out.println("[LocalFile] - reconstructing file: " + filename);

        ArrayList<Future<LocalChunk>> futureChunks = new ArrayList<>();
        chunks = new ArrayList<>();

        for (int i = 0; i < numChunks; i++) {
            futureChunks.add((Future<LocalChunk>) peerConfig.threadPool.submit(new RestoreChunk(peerConfig, new LocalChunk(fileId, i))));
            chunks.add(null);
        }

        for (Future<LocalChunk> fChunk : futureChunks) {
            LocalChunk lChunk;
            lChunk = fChunk.get();
            if (lChunk.chunk == null)
                System.out.println("[LocalFile] - Chunk " + lChunk.chunkNo + " could not be retrieved from peers");
            chunks.set(lChunk.chunkNo, lChunk);
        }

        String path = InternalState.internalStateFolder + "/restored_" + filename;
        File f = new File(path);
        f.getParentFile().mkdirs();
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f, true); // true means append
        for (int i = 0; i < chunks.size(); i++)
            if (chunks.get(i) != null && chunks.get(i).chunk != null)
                fos.write(chunks.get(i).chunk);
        fos.close();
        System.out.println("[LocalFile] - File reconstruction completed: " + path);
    }

    public void deleteFile(){
        peerConfig.threadPool.submit(new DeleteChunk(peerConfig, new LocalChunk(fileId, -2)));
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
