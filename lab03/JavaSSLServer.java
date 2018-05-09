

import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.*;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
 import java.security.Key;
 import javax.crypto.Cipher;
 import javax.crypto.Cipher;
 import javax.crypto.BadPaddingException;
 import javax.crypto.IllegalBlockSizeException;
 import javax.crypto.KeyGenerator;
 import java.security.Key;
 import java.security.InvalidKeyException;
 import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
// java -Djavax.net.ssl.keyStore=/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore -Djavax.net.ssl.keyStorePassword=sdis18 JavaSSLServer 5000 AES
//System.setProperty("javax.net.ssl.keyStore","/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore");
//System.setProperty("javax.net.ssl.keyStorePassword","sdis18");

public class JavaSSLServer {

  private static final int PORT = 8080;

  public static void main(String[] args) throws Exception {
    
    System.setProperty("javax.net.ssl.keyStore","/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore");
    System.setProperty("javax.net.ssl.keyStorePassword","sdis18");
    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
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

        System.out.println(line.getBytes());
        
        String key = "bad8deadcafef00d";
        SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] recoveredBytes = cipher.doFinal(line.getBytes());
        String recovered =  new String(recoveredBytes);
    }
    in.close();
    s.close();
  }

}