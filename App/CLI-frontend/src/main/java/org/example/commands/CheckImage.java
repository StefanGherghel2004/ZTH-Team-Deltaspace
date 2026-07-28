package org.example.commands;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CheckImage {

    private static final int MAX_SIZE = 5;
    private static CheckImage instance;

    public static CheckImage getInstance(){
        if(instance==null){
            instance=new CheckImage();
        }
        return instance;
    }

    public MultipartFile convertToMultipartFile(String filePath) throws IOException {
        if (filePath == null || filePath.trim().isEmpty()) {
            return null;
        }

        String cleanPath = filePath.replace("\"", "");

        Path path = Paths.get(cleanPath);
        if (!Files.exists(path) || Files.isDirectory(path)) {
            throw new IllegalArgumentException("File does not exist");
        }

        String originalFilename = path.getFileName().toString();
        if (!checkFormat(originalFilename.toLowerCase())) {
            throw new IllegalArgumentException("File type not supported. Only PNG, JPG, and JPEG are allowed.");
        }

        if (!checkDimension(path)) {
            throw new IllegalArgumentException("File size exceeds " + MAX_SIZE + "MB");
        }

        byte[] fileBytes = Files.readAllBytes(path);
        String contentType = "image/jpeg";
        String lowerName = originalFilename.toLowerCase();
        if (lowerName.endsWith(".png")) {
            contentType = "image/png";
        }

        return new MockMultipartFile(
                "file",
                originalFilename,
                contentType,
                fileBytes
        );
    }

    public boolean checkDimension(Path path) throws IOException {
        long size = Files.size(path);
        double MBsize= (double) size /1024/1024;
        return !(MBsize > MAX_SIZE);
    }

    public boolean checkFormat(String name) {
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }
}
