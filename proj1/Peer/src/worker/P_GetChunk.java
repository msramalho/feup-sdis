package src.worker;

import src.localStorage.StoredChunk;
import src.util.Message;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class P_GetChunk extends Protocol {
    public P_GetChunk(Dispatcher d) { super(d); }

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

            //handle ENHANCEMENT_2
            if (d.peerConfig.isEnhanced()) messageBody = new byte[0];
            else messageBody = sChunk.chunk;

            //CHUNK <Version> <SenderId> <FileId> <ChunkNo> <CRLF><CRLF><Body>
            d.peerConfig.mcRestore.send(Message.createMessage(String.format("CHUNK %s %d %s %d\r\n\r\n", d.peerConfig.protocolVersion, d.peerConfig.id, sChunk.fileId, sChunk.chunkNo), messageBody));


            if (d.peerConfig.isEnhanced()) { // ENHANCEMENT_2 continuation
                //TODO: use TCP as client
                // d.message.senderPort
                try {
                    Socket clientSocket;
                    clientSocket = new Socket(d.peerConfig.machineIp.getHostAddress(), d.message.senderPort);
                    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                    // BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    // outToServer.writeBytes(sChunk.chunk.length + "\n");
                    // inFromServer.readLine();
                    outToServer.write(sChunk.chunk, 0, sChunk.chunk.length);
                    // System.out.println("FROM SERVER: " + modifiedSentence);
                    clientSocket.close(); // sends EOF
                } catch (IOException e) {
                    System.out.println("[Protocol:GetChunk] - enhancement unable to connect to TCP");
                    e.printStackTrace();
                }
            }
        }


        sChunk.inProcess = false;
    }
}
