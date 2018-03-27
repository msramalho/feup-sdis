package src.worker;
import src.localStorage.LocalChunk;
import src.localStorage.StoredChunk;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import java.util.concurrent.ThreadLocalRandom;

public class PutChunk extends Protocol {

    public PutChunk(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {

        //Added to hashmap everytime a chunk passes through the MCgroup
        StoredChunk n = new StoredChunk(d.message.fileId, d.message.chunkNo);
        d.peerConfig.internalState.saveChunkLocally(n, d.message.body);

        try {
            int sleepFor = ThreadLocalRandom.current().nextInt(10, 401);
            System.out.println("[PutChunk] - sleep for: " + sleepFor + "ms");
            Thread.sleep(sleepFor);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        if (d.peerConfig.internalState.getStoredChunk(d.message.fileId, d.message.chunkNo).repliesToPutchunk < d.message.replicationDegree) {
            System.out.println("[Peer] - Saved chunk.");
            d.peerConfig.internalState.save();
            d.peerConfig.mcControl.send(String.format("STORED %s %d %s %d \r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, d.message.fileId, d.message.chunkNo));
        }

        //STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
    }
}
