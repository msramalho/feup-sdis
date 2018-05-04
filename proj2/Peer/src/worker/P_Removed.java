package src.worker;

import src.localStorage.StoredChunk;

public class P_Removed extends Protocol {
    P_Removed(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {
        // if i have this chunk in storage and locally saved, send it if no one else does it before me
        StoredChunk sChunk = d.peerConfig.internalState.getStoredChunk(d.message);
        sChunk.receivedPutChunk = false;
        if (sChunk != null) {
            sChunk.peersAcks.remove(d.message.senderId);
            d.peerConfig.internalState.save();
        }

        if (sChunk == null || !sChunk.isSavedLocally()) {
            System.out.println("[Protocol:Removed] - I don't have the deleted chunk:" + new StoredChunk(d.message).getShortId());
            return;
        }
        //if i have a copy and it is not null
        System.out.println("[Protocol:Removed] - trying to initiate back for chunk:" + sChunk + " - " + sChunk.receivedPutChunk);

        // if the replicationDegree is less than the number of acks then send PUTCHUNK
        if (sChunk.replicationDegree > sChunk.peersAcks.size() && !sChunk.receivedPutChunk) {
            sleepRandom();//sleep for a random amount of time
            if (!sChunk.receivedPutChunk)
                d.peerConfig.threadPool.submit(new BackupChunk(d.peerConfig, sChunk, false));
        }


    }
}
