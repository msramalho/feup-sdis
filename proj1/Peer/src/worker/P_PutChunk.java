package src.worker;

import src.localStorage.InternalState;
import src.localStorage.StoredChunk;
import src.util.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class P_PutChunk extends Protocol {

    public P_PutChunk(Dispatcher d) { super(d); }

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

        // random sleep - this peer will update the number of STORED replies to storedChunk while sleeping
        this.sleepRandom();

        //conclude the store process
        System.out.println(String.format("[P_PutChunk] - perceived replication degree: %d/%d", sChunk.countAcks(), sChunk.replicationDegree));
        if (sChunk.countAcks() < sChunk.replicationDegree) {
            d.peerConfig.internalState.saveChunkLocally(sChunk);
            sendStored(sChunk);
            d.peerConfig.internalState.save(); // commit this changes to the database non-volatile memory
        }
    }

    private void sendStored(StoredChunk sChunk) {
        //STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
        d.peerConfig.mcControl.send(Message.createMessage(String.format("STORED %s %d %s %d \r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, sChunk.fileId, sChunk.chunkNo)));
    }
}
