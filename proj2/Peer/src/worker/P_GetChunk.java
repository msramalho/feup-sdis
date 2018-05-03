package src.worker;

import src.localStorage.StoredChunk;
import src.main.Peer;
import src.main.PeerConfig;
import src.util.Message;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class P_GetChunk extends Protocol {
    P_GetChunk(Dispatcher d) { super(d); }

    @Override
    public void run() {
        // if i have this chunk in storage and locally saved, send it if no one else does it before me
        StoredChunk sChunk = d.peerConfig.internalState.getStoredChunk(d.message);

        if (sChunk == null || !sChunk.isSavedLocally()) {
            System.out.println("[Protocol:GetChunk]: I don't have the requested chunk:" + new StoredChunk(d.message).getShortId());
            return;
        }

        if (sChunk.inProcess) return;

        sChunk.inProcess = true;
        sChunk.gotAnswer = false;

        // random sleep - this peer will update the only send a CHUNK message if no other was sent
        sleepRandom();

        if (!sChunk.gotAnswer) {
            byte[] messageBody;
            String usingVersion = PeerConfig.DEFAULT_VERSION;
            boolean usingEnhancedVersion = d.peerConfig.isEnhanced() && PeerConfig.isMessageEnhanced(d.message);//both are enhanced
            //handle ENHANCEMENT_2
            if (usingEnhancedVersion) {
                messageBody = new byte[0];
                usingVersion = d.peerConfig.protocolVersion;
            } else messageBody = sChunk.chunk;

            //CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
            d.peerConfig.mcRestore.send(Message.createMessage(String.format("CHUNK %s %d %s %d\r\n\r\n", usingVersion, d.peerConfig.id, sChunk.fileId, sChunk.chunkNo), messageBody));

            if (usingEnhancedVersion) { // ENHANCEMENT_2 continuation
                try {
                    //parse the body of the message, which should contain IP:Port of the TCP socket on the other Peer
                    String[] parts = new String(d.message.body).split(":"); ;
                    String peerIp = parts[0];
                    int peerPort = Integer.parseInt(parts[1]);

                    Socket clientSocket = new Socket(peerIp, peerPort);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    outToServer.write(sChunk.chunk, 0, sChunk.chunk.length);
                    outToServer.flush();
                    clientSocket.close(); // sends EOF
                    System.out.println("[Protocol:GetChunk] - chunk " + sChunk.getShortId() + " sent through TCP (" + sChunk.chunk.length + " bytes)");
                } catch (IOException e) {
                    System.out.println("[Protocol:GetChunk] - enhancement 2 unable to connect to TCP");
                    e.printStackTrace();
                }
            }
        }


        sChunk.inProcess = false;
    }
}
