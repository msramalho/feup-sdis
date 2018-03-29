package src.worker;

import src.localStorage.StoredChunk;
import src.util.Message;


public class P_GetChunk extends Protocol {
    public P_GetChunk(Dispatcher d) { super(d); }

    @Override
    public void run() {
        // if i have this chunk in storage and locally saved, send it if no one else does it before me
        StoredChunk sChunk = d.peerConfig.internalState.getStoredChunk(d.message);

        if (sChunk == null || !sChunk.isSavedLocally()) {
            System.out.println("[Protocol:GetChunk]: I don't have the request chunk:" + new StoredChunk(d.message).getShortId());
            return;
        }

        if (sChunk.inProcess) return;

        sChunk.inProcess = true;
        sChunk.gotAnswer = false;

        // random sleep - this peer will update the only send a CHUNK message if no other was sent
        sleepRandom();

        if (!sChunk.gotAnswer) {
            //CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
            d.peerConfig.mcRestore.send(Message.createMessage(String.format("CHUNK %s %d %s %d\r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, sChunk.fileId, sChunk.chunkNo), sChunk.chunk));
        }


        sChunk.inProcess = false;
    }
}
