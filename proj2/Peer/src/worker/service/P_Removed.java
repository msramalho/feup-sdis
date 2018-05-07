package src.worker.service;

import src.localStorage.StoredChunk;
import src.worker.BackupChunk;
import src.worker.Dispatcher;
import src.worker.Protocol;

public class P_Removed extends Protocol {
    public P_Removed(Dispatcher d) { super(d); }

    @Override
    public void run() {
        // if i have this chunk in storage and locally saved, send it if no one else does it before me
        StoredChunk sChunk = d.peerConfig.internalState.getStoredChunk(d.message);
        if (sChunk != null) {
            sChunk.receivedPutChunk = false;
            sChunk.peersAcks.remove(d.message.senderId);
            d.peerConfig.internalState.save();
        }

        if (sChunk == null || !sChunk.isSavedLocally()) {
            logger.print("I don't have the deleted chunk:" + new StoredChunk(d.message).getShortId());
            return;
        }
        //if i have a copy and it is not null
        logger.print("trying to initiate back for chunk:" + sChunk + " - " + sChunk.receivedPutChunk);

        // if the replicationDegree is less than the number of acks then send PUTCHUNK
        if (sChunk.replicationDegree > sChunk.peersAcks.size() && !sChunk.receivedPutChunk) {
            sleepRandom();//sleep for a random amount of time
            if (!sChunk.receivedPutChunk)
                d.peerConfig.threadPool.submit(new BackupChunk(d.peerConfig, sChunk, false));
        }


    }
}
