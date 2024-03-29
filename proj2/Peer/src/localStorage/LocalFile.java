package src.localStorage;

import src.util.Logger;
import src.worker.BackupChunk;
import src.main.PeerConfig;
import src.worker.DeleteFile;
import src.worker.RestoreChunk;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Integer.min;

public class LocalFile {
    private PeerConfig peerConfig;
    public static Integer CHUNK_SIZE = 64000;
    public static Integer ENCRYPTION_MARGIN = 200;

    public String fileId;
    public Integer replicationDegree; //desired replication degree

    private String filename; // relative filename in the current file system
    private ArrayList<LocalChunk> chunks;
    private int numChunks;

    private boolean hasMetadata;

    private Logger logger = new Logger("LocalFile");

    public LocalFile(String filename, String notUseful, Integer replicationDegree, PeerConfig peerConfig) {
        this.peerConfig = peerConfig;
        this.replicationDegree = replicationDegree;
        this.filename = filename;
        File file = new File(this.filename);
        int file_size = (int) file.length();
        numChunks = (int) Math.ceil(file_size / CHUNK_SIZE);
    }

    public LocalFile(String filename, Integer replicationDegree, PeerConfig peerConfig) {
        this(filename, "", replicationDegree, peerConfig);
        this.hasMetadata = false;
        loadFileId(filename, "", "", -1);
    }

    public LocalFile(String filename, String creationTime, String lastModifiedTime, long size, Integer replicationDegree, PeerConfig peerConfig) {
        this(filename, "", replicationDegree, peerConfig);
        this.hasMetadata = true;
        loadFileId(filename, creationTime, lastModifiedTime, size);
    }

    public void backup() {
        logger.print("splitting file: " + filename);

        // read from the filesystem into an input stream
        File file = new File(this.filename);
        int file_size = (int) file.length();
        FileInputStream inStream;
        try {
            inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.err("unable to read file: " + this.filename);
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
            logger.print(String.format("Chunk %d has %d bytes (read: %d out of %d)", i, chunkSize, totalBytesRead, file_size));
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
        logger.print("reconstructing file: " + filename);

        ArrayList<Future<LocalChunk>> futureChunks = new ArrayList<>();
        chunks = new ArrayList<>();

        if(hasMetadata) {
            LocalChunk tempLocalChunk = new LocalChunk(fileId, 0);
            if(peerConfig.internalState.getLocalChunk(tempLocalChunk.getUniqueId()) == null) {
                for(int i = 0; i <= numChunks; i++) {
                    peerConfig.internalState.addLocalChunk(new LocalChunk(fileId, i));
                }
            }

            peerConfig.internalState.save();
        }

        for (int i = 0; i <= numChunks; i++) {
            futureChunks.add((Future<LocalChunk>) peerConfig.threadPool.submit(new RestoreChunk(peerConfig, new LocalChunk(fileId, i))));
            chunks.add(null);
        }

        for (Future<LocalChunk> fChunk : futureChunks) {
            LocalChunk lChunk;
            lChunk = fChunk.get();
            
            if (lChunk == null || lChunk.chunk == null) {
                logger.print("at least one chunk could not be retrieved from peers...aborting");
                return;
            } else if ((lChunk.chunkNo != numChunks && lChunk.chunk.length == 0)) {
                logger.print("received a 0 byte chunk that is not the last...aborting");
                return;
            } else if ((lChunk.chunkNo != numChunks && lChunk.chunk.length > (LocalFile.CHUNK_SIZE + ENCRYPTION_MARGIN))) {
                logger.print("received a " + lChunk.chunk.length + " byte chunk that is not the last, should always be: " + (LocalFile.CHUNK_SIZE + ENCRYPTION_MARGIN) + "...aborting");
                return;
            }
            chunks.set(lChunk.chunkNo, lChunk);
        }

        String path = InternalState.internalStateFolder + "/restored_" + filename;
        File f = new File(path);
        f.delete(); //delete the file if it already exists so that there the data is not appended to the old file
        f.getParentFile().mkdirs();
        f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f, true); // true means append
        for (LocalChunk chunk : chunks)
            if (chunk != null && chunk.chunk != null) {
            	chunk.decryptBytes();
                fos.write(chunk.chunk);
            }
        fos.close();
        logger.print("File reconstruction completed: " + path);
    }

    public void deleteFile() {
        peerConfig.threadPool.submit(new DeleteFile(peerConfig, new LocalChunk(fileId, -2)));
    }

    public void loadFileId(String filename, String creationTime, String lastModifiedTime, long size) {
        if (creationTime.isEmpty() || lastModifiedTime.isEmpty() || size == -1) {
            try {
                BasicFileAttributes metadata = Files.readAttributes(Paths.get(filename), BasicFileAttributes.class);
                createFileId(filename, metadata.creationTime().toString(), metadata.lastModifiedTime().toString(), metadata.size());
            } catch (IOException e) {
                logger.err("Unable to read file's metadata, using filename only for the chunk");
            }
        } else {
            createFileId(filename, creationTime, lastModifiedTime, size);
        }
    }

    private void createFileId(String filename, String creationTime, String lastModifiedTime, long size) {
        String hashSource = filename + creationTime + lastModifiedTime + size;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(hashSource.getBytes(StandardCharsets.UTF_8));
            fileId = String.format("%064x", new BigInteger(1, hash));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
