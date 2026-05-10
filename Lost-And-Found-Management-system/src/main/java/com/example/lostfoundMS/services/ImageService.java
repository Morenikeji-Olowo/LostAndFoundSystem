package com.example.lostfoundMS.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageService {
    @Value("${app.upload.dir}")
    private String uploadDir;

    public String saveImage(MultipartFile file, String referenceCode) throws IOException{
        if(file == null ||file.isEmpty() ){
            return null;
        }
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename
                .substring(originalFilename.lastIndexOf("."));

        String fileName = referenceCode + extension;

        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/items/" + fileName;


    }


}
