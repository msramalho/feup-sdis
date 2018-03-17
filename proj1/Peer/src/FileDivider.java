
package LocalFile();

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


public class FileDivider {
    //----- FILE ------
    String id;
    String fileName; // path + filename in the current file system
    ArrayList<LocalChunk> chunks;
    Integer replicationDegree; //desired replication degree
    //-----------------

    int chunk_Size = 64000;
    int chunck_id;
    int chunk_name;

    byte[] temporary = null;
    int totalBytesRead = 0;

    public FileDivider(String fileName , replica){
        File file = new File(fileName);
        this.id = "0001";
        this.fileName = fileName;
        replicationDegree = 0;

    }

    public splitFile(){

        file_size = (int) file.length();
        System.out.println("[ File Received: Length " + file_size + " ]");

        InputStream inStream = null;
        int totalBytesRead = 0;

        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
            System.out.println(inStream);
            int i = 0;
            while(totalBytesRead < file_size){

                int bytesRemaining = file_size-totalBytesRead;

                temporary = new byte[chunk_Size]; //Temporary Byte Array
                try {
                    int bytesRead = inStream.read(temporary, 0, chunk_Size);
                    System.out.println("Chunk["+i+"]" + " size: " + bytesRead);
                    i++;
                } catch (IOException e){
                    System.out.println("[ CAN'T READ Chunk ]");
                }

                totalBytesRead+=chunk_Size;
            }

        } catch (FileNotFoundException ex) {
            System.out.println("[ CAN'T READ File ]");
        }
    }
}
