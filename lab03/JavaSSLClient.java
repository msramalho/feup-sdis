
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.ssl.SSLSocketFactory;
import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManagerFactory;
import com.sun.net.ssl.TrustManager;
import java.security.MessageDigest;

// javac -Xlint:deprecation JavaSSLClient.java
@SuppressWarnings("deprecation")
public class JavaSSLClient{

  private static final String HOST = "localhost";

  private static final int PORT = 8080;

  public static void main(String[] args) throws Exception {
    System.setProperty("javax.net.ssl.trustStore","/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore");
    System.setProperty("javax.net.ssl.trustStorePassword","sdis18");

    char[] passphrase = "sdis18".toCharArray();
    KeyStore keystore = KeyStore.getInstance("JKS");
    keystore.load(new FileInputStream("/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore"), passphrase);

    //This class acts as a factory for trust managers based on a source of trust material. 
    //Each trust manager manages a specific type of trust materia. It uses keystore
    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
    tmf.init(keystore);

    SSLContext context = SSLContext.getInstance("TLS");
    TrustManager[] trustManagers = tmf.getTrustManagers();

    context.init(null, trustManagers, null);

    SSLSocketFactory sf = context.getSocketFactory();

    Socket s = sf.createSocket(HOST, PORT);
    OutputStream out = s.getOutputStream();
    out.write("\nConnection established.\n\n".getBytes());

    //int theCharacter = 0;
    //theCharacter = System.in.read();

    MessageDigest messageDigest;

    String data = "DELETE CHUNK";

    try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte bytes : messageDigestMD5) {
                stringBuffer.append(String.format("%02x", bytes & 0xff));
            }
 
            System.out.println("data:" + data);
            System.out.println("digestedMD5(hex):" + stringBuffer.toString());
            out.write(stringBuffer.toString().getBytes());

        } catch (NoSuchAlgorithmException exception) {
            // TODO Auto-generated catch block
            exception.printStackTrace();
        }

    /*while (theCharacter != '~') // The '~' is an escape character to exit
    {
        /*try {
            String data = theCharacter + "";
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes());
            byte[] messageDigestMD5 = messageDigest.digest();
            StringBuffer stringBuffer = new StringBuffer();
            for (byte bytes : messageDigestMD5) {
                stringBuffer.append(String.format("%02x", bytes & 0xff));
            }
 
            System.out.println("data:" + data);
            //System.out.println("digestedMD5(hex):" + stringBuffer.toString());
        } catch (NoSuchAlgorithmException exception) {
            // TODO Auto-generated catch block
            exception.printStackTrace();
        }

        System.out.println("CAR: " + theCharacter);
        out.write(theCharacter);
        out.flush();
        System.out.println("VAI LER ");
        theCharacter = System.in.read();
    }*/

    out.close();
    s.close();
  }
}
