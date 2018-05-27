package src.worker.service;

import src.localStorage.LocalChunk;
import src.worker.DeleteFile;
import src.worker.Dispatcher;
import src.worker.Protocol;

import java.util.ArrayList;

public class P_Goodbye extends Protocol {
    public P_Goodbye(Dispatcher d) {super(d);}

    @Override
    public void run() {
        System.out.println("RECEIVING MESSAGE-............."); 
        ArrayList<String> filesFound = new ArrayList<>();
        // iterate over all local chunks
        for (LocalChunk localChunk : d.peerConfig.internalState.localChunks.values()) {
            // if this chunk has been deleted and the peer that said HELLO is saving this chunk, send DELETE
            System.out.println("RECEIVING MESSAGE");   
        }   
    }
}
