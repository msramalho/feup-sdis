package src.worker;

import src.localStorage.Chunk;
import src.localStorage.StoredChunk;

// save information about what other peers are saying in the network so we can respect replication degrees
// only receives STORED that are not about local files, those go to the worker.BackupChunk
public class P_Stored extends Protocol {

    public P_Stored(Dispatcher d) { super(d); }

    @Override
    public void run() {
        // check if this STORED is about a P_Stored P_Chunk
        Chunk chunk = d.peerConfig.internalState.getStoredChunk(d.message);

        // if not, check if this STORED is about a Local P_Chunk
        if (chunk == null) chunk = d.peerConfig.internalState.getLocalChunk(d.message);

        // if not, it is about a never-heard-off chunk, so it is not local, add as new (not locally saved) P_Stored P_Chunk
        if (chunk == null) {
            chunk = new StoredChunk(d.message);
            d.peerConfig.internalState.addStoredChunk((StoredChunk) chunk).save();
        }

        chunk.addAck(d.message.senderId);
        d.peerConfig.internalState.save();
    }
}
