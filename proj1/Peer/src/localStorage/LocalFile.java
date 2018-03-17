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
import java.util.concurrent.ExecutorService;


public class LocalFile {
    static Integer CHUNCK_SIZE = 64000;
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

    public void splitFile(){
        int totalBytesRead = 0;

        System.out.println("LocalFile: " + filename);

        File file = new File(this.filename);
        int file_size = (int) file.length();
        System.out.println("[ File Received: Length " + file_size + " ]");

        InputStream inStream = null;

        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
            int i = 0;
            while(totalBytesRead < file_size){

                int bytesRemaining = file_size-totalBytesRead;

                byte[] temporaryChunk = new byte[LocalFile.CHUNCK_SIZE]; //Temporary Byte Array
                try {
                    int bytesRead = inStream.read(temporaryChunk, 0, LocalFile.CHUNCK_SIZE);
                    System.out.println("Chunk["+i+"]" + " size: " + bytesRead);
                    BackupChunkWorker bcWorker = new BackupChunkWorker(peerConfig, temporaryChunk, i, this.replicationDegree);
                    this.peerConfig.threadPool.submit(bcWorker);
                    i++;
                } catch (IOException e){
                    System.out.println("[ CAN'T READ Chunk ]");
                }

                totalBytesRead+=LocalFile.CHUNCK_SIZE;
            }

        } catch (FileNotFoundException ex) {
            System.out.println("[ CAN'T READ File ]");
        }
    }
}
