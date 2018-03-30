package src.worker;

import src.localStorage.Chunk;
import src.localStorage.LocalFile;
import src.localStorage.StoredChunk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

public class P_Chunk extends Protocol {
    public P_Chunk(Dispatcher d) { super(d); }

    @Override
    public void run() {
        Chunk chunk;

        // check if this CHUNK is about a StoredChunk
        if ((chunk = d.peerConfig.internalState.getStoredChunk(d.message)) != null) {
            ((StoredChunk) chunk).gotAnswer = true;
        } // if not, check if this CHUNK is about a LocalChunk
        else if ((chunk = d.peerConfig.internalState.getLocalChunk(d.message)) != null) {
            if (!d.peerConfig.isEnhanced() || d.message.body.length != 0) {
                chunk.chunk = d.message.body; // save the received value
            } else try {
                ServerSocket welcomeSocket = new ServerSocket();
                welcomeSocket.setReuseAddress(true);
                welcomeSocket.bind(new InetSocketAddress(d.peerConfig.mcRestore.getLocalPort()));
                System.out.println("got it");
                //TODO: figure out a way to resue port or to set a new port
                Socket connectionSocket = welcomeSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                // DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                char[] incomingChars = new char[LocalFile.CHUNK_SIZE];
                int read = inFromClient.read(incomingChars, 0, LocalFile.CHUNK_SIZE);
                System.out.println("Read: " + read + " bytes");
                chunk.chunk = toBytes(incomingChars);
                System.out.println("Received: " + chunk.chunk.length + " bytes");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        return Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
    }
}
