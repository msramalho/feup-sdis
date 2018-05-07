
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;
import javax.crypto.KeyGenerator;
import java.security.SecureRandom;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import java.*;
import javax.crypto.Cipher;
 
// RUN CMD
// java -Djavax.net.ssl.trustStore=/home/diogo/Github/feup-sdis/lab03/mykeystore/examplestore -Djavax.net.ssl.trustStorePassword=sdis18 JavaSSLClient localhost 5000 + 1,1,1 AES

public class JavaSSLClient {
    
    // Default Port 
    static final int port = 8000;
 
    public static void main(String[] args) {
        
        String host = args[0];
        // User's PORT
        int port = Integer.parseInt(args[1]);
        String oper = args[2];
        String opnd = args[3];
        String[] list = opnd.split(",");
        String cypher = args[4];

        SSLSocketFactory sslSocketFactory = 
                (SSLSocketFactory)SSLSocketFactory.getDefault();
        try {
            Socket socket = sslSocketFactory.createSocket("localhost", port);
            //socketStrem to receive server communication
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            
            try (BufferedReader bufferedReader = 
                    new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                Scanner scanner = new Scanner(System.in);
                
                while(true){
                    System.out.println("Enter Message:");
                    
                    String inputLine = scanner.nextLine();

                    if(inputLine.equals("q")){
                        break;
                    }
                     
                    
                    out.println(inputLine);
                    System.out.println(bufferedReader.readLine());
                }
            }
             
        } catch (IOException ex) {
            Logger.getLogger(JavaSSLClient.class.getName())
                    .log(Level.SEVERE, null, ex);
        }     
    }

    public static String AesDecrypt(String encryptContent, String password) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(password.getBytes());
            keyGen.init(128, secureRandom);
            //SecretKey secretKey = keyGen.generateKey();
            byte[] enCodeFormat = hexStringToByteArray("sdis18");
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(hexStringToByteArray(encryptContent)));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] hexStringToByteArray(String s) {   
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
    }
    return data;
}
     
}