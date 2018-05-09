

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;

// RUN CMD
// java -Djavax.net.ssl.keyStore=/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore -Djavax.net.ssl.keyStorePassword=sdis18 JavaSSLServer 5000 AES
//System.setProperty("javax.net.ssl.keyStore","/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore");
//System.setProperty("javax.net.ssl.keyStorePassword","sdis18");

public class JavaSSLServer {

  private static final int PORT = 8080;

  public static void main(String[] args) throws Exception {
    
    System.setProperty("javax.net.ssl.keyStore","/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore");
    System.setProperty("javax.net.ssl.keyStorePassword","sdis18");

    char[] passphrase = "sdis18".toCharArray();
    KeyStore keystore = KeyStore.getInstance("JKS");
    keystore.load(new FileInputStream("/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore"), passphrase);
    
    // Each key manager manages a specific type of key material for use by secure sockets
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    //Get Session
    kmf.init(keystore, passphrase);
    SSLContext context = SSLContext.getInstance("TLS");
    KeyManager[] keyManagers = kmf.getKeyManagers();

    context.init(keyManagers, null, null);

    SSLServerSocketFactory ssf = context.getServerSocketFactory();
    ServerSocket ss = ssf.createServerSocket(PORT);

    System.out.println("Wating for client...");

    Socket s = ss.accept();

    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));

    String line = null;

    while (((line = in.readLine()) != null)) {

        System.out.println(line);
    }
    in.close();
    s.close();
  }
}