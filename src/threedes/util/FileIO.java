package threedes.util;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileIO {

    public static String readFileToString(String file, Charset charSet)
    {
        if (charSet == null)
        {
            charSet = StandardCharsets.UTF_8;
        }
        
        String inputText = null;
        try {
            inputText = new String(Files.readAllBytes(Paths.get(file)), charSet);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return inputText;
    }
    
    public static byte [] readFileToByteArray(String file)
    {
        byte [] inputBytes = null;
        try {
            inputBytes = Files.readAllBytes(Paths.get(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return inputBytes;
    }
    
    public static void writeBinaryFile(String file, byte [] content)
    {
        try {
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(new File(file)));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void writeTextFile(String file, String content)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(file)));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
