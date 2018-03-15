package localStorage;
import java.util.ArrayList;
import java.util.ArrayList;
import java.io.*;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.zip.CRC32;
import javax.swing.*;



public class LocalFile {
    static Integer CHUCNK_SIZE = 64000;
    String id;
    String filename; // path + filename in the current file system
    ArrayList<LocalChunk> chunks;
    Integer replicationDegree; //desired replication degree

    public LocalFile(String id, String filename, Integer replicationDegree) {
        this.id = id;
        this.filename = filename;
        this.chunks = new ArrayList<LocalChunk>();
        this.replicationDegree = replicationDegree;
    }

    public void splitFile(){

        byte[] temporary = null;
        int totalBytesRead = 0;

        System.out.println("LocalFile: " + filename);

        File file = new File(this.filename);
        int file_size = (int) file.length();
        System.out.println("[ File Received: Length " + file_size + " ]");

        InputStream inStream = null;

        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
            System.out.println(inStream);
            int i = 0;
            while(totalBytesRead < file_size){

                int bytesRemaining = file_size-totalBytesRead;

                temporary = new byte[LocalFile.CHUCNK_SIZE]; //Temporary Byte Array
                try {
                    int bytesRead = inStream.read(temporary, 0, LocalFile.CHUCNK_SIZE);
                    System.out.println("Chunk["+i+"]" + " size: " + bytesRead);
                    i++;
                } catch (IOException e){
                    System.out.println("[ CAN'T READ Chunk ]");
                }

                totalBytesRead+=LocalFile.CHUCNK_SIZE;
            }

        } catch (FileNotFoundException ex) {
            System.out.println("[ CAN'T READ File ]");
        }
    }
}
