package src.worker;

// save information about what other peers are saying in the network so we can respect replication degrees
// only receives STORED that are not about local files, those go to the worker.BackupChunk
public class Stored extends Protocol {

    public Stored(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {
        d.peerConfig.internalState.updateExternalPutchunkReplies(d.message.fileId, d.message.chunkNo);
    }
}
