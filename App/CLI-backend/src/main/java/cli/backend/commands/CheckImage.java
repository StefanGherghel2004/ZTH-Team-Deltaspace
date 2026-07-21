package cli.backend.commands;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class CheckImage {

    private static CheckImage instance;
    public static CheckImage getInstance(){
        if(instance==null){
            instance=new CheckImage();
        }
        return instance;
    }

    public String processAndSaveImage(String filePath) throws  IOException{
        if(filePath==null || filePath.trim().isEmpty()){
            return null;
        }
        Path sourcePath = Paths.get(filePath);

        if(!Files.exists(sourcePath) || Files.isDirectory(sourcePath)){
            throw new IllegalArgumentException("File does not exist");
        }

        if(!checkDimension(sourcePath)){
            throw new IllegalArgumentException("File size exceeds 2MB limit!");
        }

        String name = sourcePath.getFileName().toString().toLowerCase();
        if(!checkFormat(name)){
            throw  new IllegalArgumentException("File type not supported");
        }

        Path assetsDir = Paths.get("assets", "images");
        Files.createDirectories(assetsDir);

        String newFileName = System.currentTimeMillis() + "_" + sourcePath.getFileName();
        Path targetPath = assetsDir.resolve(newFileName);
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

        return targetPath.toString();
    }

    public boolean checkDimension(Path path) throws IOException {
        long size = Files.size(path);
        double MBsize= (double) size /1024/1024;
        return !(MBsize > 2);
    }

    public boolean checkFormat(String name) {
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }
}
