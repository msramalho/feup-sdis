package src.worker;

import src.localStorage.StoredChunk;

import java.util.concurrent.ThreadLocalRandom;

public class PutChunk extends Protocol {

    public PutChunk(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {

        // try to read the chunk from the internal state, and add it if it is not there
        StoredChunk sChunk = d.peerConfig.internalState.getStoredChunk(d.message);
        if (sChunk == null) {
            sChunk = new StoredChunk(d.message);
            d.peerConfig.internalState.addStoredChunk(sChunk);
        } else {
            // this putchunk is already handled by the current peer, abort
            // if the current peer already has a copy of this chunk, send a STORED anyway (UDP is unreliable)
            if (sChunk.isSavedLocally()) sendStored(sChunk);
            return;
        }

        // random sleep
        try {
            int sleepFor = ThreadLocalRandom.current().nextInt(10, 401);
            System.out.println("[PutChunk] - sleep for: " + sleepFor + "ms");
            // this peer will update the number of STORED replies to storedChunk while sleeping
            Thread.sleep(sleepFor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //conclude the store process
        System.out.println(String.format("[PutChunk] - perceived replication degree: %d/%d", sChunk.countAcks(), sChunk.replicationDegree));
        if (sChunk.countAcks() < sChunk.replicationDegree) {
            d.peerConfig.internalState.saveChunkLocally(sChunk);
            sendStored(sChunk);
            d.peerConfig.internalState.save(); // commit this changes to the database non-volatile memory
        }
    }

    private void sendStored(StoredChunk sChunk) {
        //STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
        d.peerConfig.mcControl.send(String.format("STORED %s %d %s %d \r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, sChunk.fileId, sChunk.chunkNo));
    }
}
