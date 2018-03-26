package src.worker;

import src.main.PeerConfig;
import src.util.Message;

public class Dispatcher implements Runnable {
    Message message;
    PeerConfig peerConfig;

    public Dispatcher(Message message, PeerConfig peerConfig) {
        this.message = message;
        this.peerConfig = peerConfig;
    }

    @Override
    public void run() {
        Protocol p = null;

        // dispatch the message to the proper protocol handler
        if (message.isPutchunk())
            p = new PutChunk(this);
        else if (message.isStored())
            p = new Stored(this);
        // else if (message.isGetChunk())
        //     p = new GetChunk()


        if (p != null) p.run();
    }
}
