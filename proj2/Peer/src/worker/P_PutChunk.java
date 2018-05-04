package src.worker;

import src.localStorage.StoredChunk;
import src.util.Message;

public class P_PutChunk extends Protocol {

    P_PutChunk(Dispatcher d) { super(d); }

    @Override
    public void run() {
        // try to read the chunk from the internal state, and add it if it is not there
        StoredChunk sChunk = d.peerConfig.internalState.getStoredChunk(d.message);
        if (sChunk == null) {
            sChunk = new StoredChunk(d.message);
            d.peerConfig.internalState.addStoredChunk(sChunk);
        } else {
            sChunk.receivedPutChunk = true; // only need for not-new chunks
            // this putchunk is already handled by the current peer, abort
            // if the current peer already has a copy of this chunk, send a STORED anyway (UDP is unreliable)
            if (sChunk.isSavedLocally() && !sChunk.deleted) {
                sendStored(sChunk); return;
            } else if (sChunk.countAcks() < sChunk.replicationDegree) // save previously deleted chunk (sChunk.deleted &&
                sChunk.chunk = d.message.body;
            else return;
        }

        // random takes ENHANCEMENT_1 into account sleep - this peer will update the number of STORED replies to storedChunk while sleeping
        sleepRandom();

        //conclude the store process
        System.out.println(String.format("[Protocol:PutChunk] - perceived replication degree for chunk %s: %d/%d", sChunk.getShortId(), sChunk.countAcks(), sChunk.replicationDegree));
        if (sChunk.countAcks() < sChunk.replicationDegree) {

            // check if enough disk space is available
            if (d.peerConfig.internalState.availableSpace() < d.message.body.length) { // if there isn't enough memory, try to free
                System.out.println("[Protocol:PutChunk] - there isn't enough memory for chunk: " + sChunk.getShortId());
                if (!d.peerConfig.internalState.freeMemory(d.peerConfig, d.message.body.length)) { // if unable to free memory
                    System.out.println("[Protocol:PutChunk] - and cannot free enough memory for chunk: " + sChunk.getShortId());
                    return;
                }
            }

            //if it reaches here then there is enough memory or enough memory is being freed asynchronously
            d.peerConfig.internalState.saveChunkLocally(sChunk);
            sendStored(sChunk);
            d.peerConfig.internalState.save(); // commit this changes to the database non-volatile memory
        }
    }

    private void sendStored(StoredChunk sChunk) {
        //STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
        d.peerConfig.mcControl.send(Message.createMessage(String.format("STORED %s %d %s %d\r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, sChunk.fileId, sChunk.chunkNo)));
    }
}
