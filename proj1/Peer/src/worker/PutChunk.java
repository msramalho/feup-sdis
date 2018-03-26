package src.worker;

import java.util.concurrent.ThreadLocalRandom;

public class PutChunk extends Protocol {

    public PutChunk(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {
        // d.peerConfig.int
        // d.message.replicationDegree
        try {
            int sleepFor = ThreadLocalRandom.current().nextInt(0, 401);
            System.out.println("[Dispatcher] - sleep for: " + sleepFor + "ms");
            Thread.sleep(sleepFor);
            //only save chunk locally if not enough peers in the network have answered saying they saved the chunk
            if (d.peerConfig.internalState.getStoredChunk(d.message.fileId, d.message.chunkNo).repliesToChunk < d.message.replicationDegree){

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
        d.peerConfig.mcControl.send(String.format("STORED %s %d %s %d \r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, d.message.fileId, d.message.chunkNo));
    }
}
