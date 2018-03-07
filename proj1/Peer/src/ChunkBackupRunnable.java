import java.net.MulticastSocket;

public class ChunkBackupRunnable {
    MulticastSocket mcControl; // Multicast Control Socket
    PeerConfig peerConfig;

    public ChunkBackupRunnable(MulticastSocket mcControl, PeerConfig peerConfig) {
        this.mcControl = mcControl;
        this.peerConfig = peerConfig;
    }

    /**
     * Send a chunk to the mcc
     * PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
     * @param chunk
     */
    public void backupChunk(String chunk, int chunkNo, int replicationDef){
        String message = String.format("PUTCHUNK %s %d %d %d <CRLF>", peerConfig.protocolVersion, peerConfig.id, chunkNo, replicationDef);
    }
}
