import java.io.IOException;
import java.net.*;
import java.io.*;

public class FileDividerMain {

    public static void main(String[] args) {
        System.out.println("--- [ SERVER ] ---");
        FileReader txt = null;

        File file = new File("file.txt");
        FileDivider createChunk = new FileDivider(file);

    }
}
