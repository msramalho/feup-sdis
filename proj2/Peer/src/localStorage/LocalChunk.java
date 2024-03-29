package src.localStorage;

import src.main.PeerConfig;
import src.util.Cryptography;
import src.util.TcpServer;

import java.io.*;

public class LocalChunk extends Chunk implements Serializable {
    public transient TcpServer tcp = null;

    public LocalChunk(String fileId, int chunkNo) { super(fileId, chunkNo); }

    public LocalChunk(String fileId, int chunkNo, Integer replicationDegree, byte[] chunk) {
      super(fileId, chunkNo, replicationDegree, chunk);
      this.encryptBytes();
   }

    // uses the previously opened ServerSocket to receive the chunk bytes through TCP
    public boolean loadFromTCP() {
        chunk = tcp.receive();
        return chunk != null;
    }

    public boolean startTCP() {
        tcp = tcp == null ? new TcpServer() : tcp; // singleton
        return tcp.start();
    }

    public boolean noTcp() {
        return tcp == null || tcp.dead();
    }
}
