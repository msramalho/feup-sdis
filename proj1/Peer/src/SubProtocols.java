import java.io.IOException;

public class SubProtocols {
    PeerConfig peerConfig;

    public SubProtocols() {
    }

    public SubProtocols(PeerConfig peerConfig) {
        this.peerConfig = peerConfig;
    }

    public void sendChunk(String fileId) {
        // PUTCHUNK <Version> <SenderId> <FileId> <ChunkNo> <ReplicationDeg> <CRLF><CRLF><Body>
        try {
            this.peerConfig.mcControl.setTimeToLive(1000);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
