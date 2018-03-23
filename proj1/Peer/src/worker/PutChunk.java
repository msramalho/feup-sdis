package src.worker;

import java.util.concurrent.ThreadLocalRandom;

public class PutChunk extends Protocol {

    public PutChunk(Dispatcher d) {
        super(d);
    }

    @Override
    public void run() {
        // d.peerConfig.int
        try {
            int sleepFor = ThreadLocalRandom.current().nextInt(0, 401);
            Thread.sleep(sleepFor);
            System.out.println("[Dispatcher] - sleep for: " + sleepFor + "ms");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //STORED <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF>
        d.peerConfig.mcControl.send(String.format("STORED %s %d %s %d \r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, d.message.fileId, d.message.chunkNo));
    }
}
