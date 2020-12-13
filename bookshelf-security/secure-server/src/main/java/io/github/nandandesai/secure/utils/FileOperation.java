package io.github.nandandesai.secure.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileOperation {

    public static boolean writeFile(String path, String fileName, byte[] data) throws IOException {
        String filePath = path + fileName;
        File file = new File(filePath);
        if (!file.createNewFile()) {
            return false; //return false if file already exists
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        return true;
    }

    public static byte[] readFile(String path, String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(path + fileName));
    }

    public static boolean deleteFile(String path, String fileName) throws IOException {
        return Files.deleteIfExists(Paths.get(path + fileName));
    }

}
