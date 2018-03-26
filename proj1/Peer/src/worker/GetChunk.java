package src.worker;

import src.localStorage.StoredChunk;

public class GetChunk extends Protocol {
    public GetChunk(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {
        //se eu tiver esta chunk devolve a chunk
        StoredChunk storedChunk = d.peerConfig.internalState.getStoredChunk(d.message.fileId, d.message.chunkNo);
        if (storedChunk != null) {
            // d.peerConfig.mcRestore.send(String.format("CHUNK ... \r\n\r\n%s", storedChunk.readChunkFromMemory()));
        }
    }
}
