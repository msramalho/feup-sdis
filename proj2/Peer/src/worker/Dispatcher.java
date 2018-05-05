package src.worker;

import src.main.PeerConfig;
import src.util.Message;
import src.worker.service.*;

public class Dispatcher implements Runnable {
    public Message message;
    public PeerConfig peerConfig;

    public Dispatcher(Message message, PeerConfig peerConfig) {
        this.message = message;
        this.peerConfig = peerConfig;
    }

    @Override
    public void run() {
        Protocol p = null;

        // dispatch the message to the proper protocol handler
        if (message.isPutchunk())
            p = new P_PutChunk(this);
        else if (message.isStored())
            p = new P_Stored(this);
        else if (message.isGetChunk())
            p = new P_GetChunk(this);
        else if (message.isChunk())
            p = new P_Chunk(this);
        else if (message.isDelete())
            p = new P_Delete(this);
        else if (message.isRemoved())
            p = new P_Removed(this);
        else if (message.isHello())
            p = new P_Hello(this);
        else if (message.isDeleted())
            p = new P_Deleted(this);


        if (p != null) p.run();
    }
}
