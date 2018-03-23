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
        // System.out.println("\n[Dispatcher] - HELLO, this is d with message:\n" + message.toString());

        Protocol p = null;

        if (message.isPutchunk()) {
            p = new PutChunk(this);
        }

        if (p != null) p.run();
    }
}
