
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocketFactory;
 
// RUN CMD
// java -Djavax.net.ssl.keyStore=/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore -Djavax.net.ssl.keyStorePassword=sdis18 JavaSSLServer 5000 AES

public class JavaSSLServer {
     
    static final int port = 8000;
 
    public static void main(String[] args) {
         
         int port = Integer.parseInt(args[0]);
         String cypher = args[1];
         
        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
         
        try {
            ServerSocket sslServerSocket = 
                    sslServerSocketFactory.createServerSocket(port);
            System.out.println("SSL ServerSocket started");
            System.out.println(sslServerSocket.toString());
             
            Socket socket = sslServerSocket.accept();
            
            System.out.println("ServerSocket accepted");
             
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            try (BufferedReader bufferedReader = 
                
                    new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                
                String line;
                while((line = bufferedReader.readLine()) != null){
                    //Server GET Message
                    System.out.println(line + " - FROM CLIENT");
                    //Server SEND Message
                    out.println(line + " - FROM SERVER");
                }
            }
            System.out.println("Closed");
             
        } catch (IOException ex) {
            Logger.getLogger(JavaSSLServer.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
     
}