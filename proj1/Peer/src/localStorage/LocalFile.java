package src.localStorage;

import src.worker.BackupChunk;
import src.main.PeerConfig;
import src.worker.DeleteFile;
import src.worker.RestoreChunk;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
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
        numChunks = (int) Math.ceil(file_size / CHUNK_SIZE);
    }

    public void backup() {
        System.out.println("[LocalFile] - splitting file: " + filename);

        // read from the filesystem into an input stream
        File file = new File(this.filename);
        int file_size = (int) file.length();
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("[LocalFile] - unable to read file: " + this.filename);
            return;
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
            peerConfig.threadPool.submit(bcWorker);
            i++;
            System.out.println(String.format("Chunk %d has %d bytes (read: %d out of %d)", i, chunkSize, totalBytesRead, file_size));
        }
        if ((file_size % CHUNK_SIZE) == 0) // if last chunk is 64K send chunk with size 0
            peerConfig.threadPool.submit(new BackupChunk(peerConfig, new LocalChunk(fileId, i, replicationDegree, new byte[0]), true));

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

        for (int i = 0; i <= numChunks; i++) {
            futureChunks.add((Future<LocalChunk>) peerConfig.threadPool.submit(new RestoreChunk(peerConfig, new LocalChunk(fileId, i))));
            chunks.add(null);
        }

        for (Future<LocalChunk> fChunk : futureChunks) {
            LocalChunk lChunk;
            lChunk = fChunk.get();
            // System.out.println(String.format("chunk %d of %d has %d bytes", lChunk.chunkNo, numChunks, lChunk.chunk.length));
            if (lChunk == null || lChunk.chunk == null) {
                System.out.println("[LocalFile] - at least one chunk could not be retrieved from peers...aborting");
                return;
            } else if ((lChunk.chunkNo != numChunks && lChunk.chunk.length == 0)) {
                System.out.println("[LocalFile] - received a 0 byte chunk that is not the last...aborting");
                return;
            } else if ((lChunk.chunkNo != numChunks && lChunk.chunk.length != LocalFile.CHUNK_SIZE)) {
                System.out.println("[LocalFile] - received a " + lChunk.chunk.length + " byte chunk that is not the last, should always be: " + LocalFile.CHUNK_SIZE + "...aborting");
                return;
            }


            chunks.set(lChunk.chunkNo, lChunk);
        }

        String path = InternalState.internalStateFolder + "/restored_" + filename;
        File f = new File(path);
        f.getParentFile().mkdirs();
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f, true); // true means append
        for (int i = 0; i < chunks.size(); i++){
            System.out.println("[ BEATRIZ ] : " + chunks.get(i).chunk.getBytes);
            if (chunks.get(i) != null && chunks.get(i).chunk != null)
                fos.write(chunks.get(i).chunk);
        }
        fos.close();
        System.out.println("[LocalFile] - File reconstruction completed: " + path);
    }

    public void deleteFile() {
        peerConfig.threadPool.submit(new DeleteFile(peerConfig, new LocalChunk(fileId, -2)));
    }


    private void loadFileId() {
        String hashSource = filename;
        try {
            BasicFileAttributes metadata = Files.readAttributes(Paths.get(filename), BasicFileAttributes.class);
            hashSource = filename + metadata.creationTime() + metadata.lastModifiedTime() + metadata.size();
        } catch (IOException e) {
            System.out.println("[LocalFile] - Unable to read file's metadata, using filename only for the chunk");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(hashSource.getBytes(StandardCharsets.UTF_8));
            fileId = String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
