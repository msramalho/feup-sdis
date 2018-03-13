import java.io.IOException;
import java.net.*;
import java.io.*;

public class FileDividerMain {

    public static void main(String[] args) {
        System.out.println("---------------------------- [ SERVER ] -------------------------------");
        FileReader txt = null;

        File file = new File(args[0]);
        FileDivider createChunk = new FileDivider(file, 64000);

    }
}
