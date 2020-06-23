/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.multipart;

import com.starturn.engine.multipart.exception.FileStorageException;
import com.starturn.engine.multipart.exception.MyFileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Administrator
 */
@Service
public class PictureFileStorageService {

    private final Path fileStorageLocation;
    String imagePath = "";

    public PictureFileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getProfilePicDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
            imagePath = fileStorageProperties.getProfilePicDir();
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory "
                    + "where the uploaded files will be stored", ex);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        //normalise file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String newFileName;
        if (fileName.contains("..")) {
            throw new FileStorageException("Filename contains invalid path sequence: " + fileName);
        }
        //retrieve file extension
        String ext = FilenameUtils.getExtension(fileName); // returns "txt"
        String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), ext);
        //copy file to target location, replacing existing file with the same name
        File dir = new File(imagePath);
        File newFile = new File(dir, name);
        newFileName = newFile.getCanonicalPath();
        Path targetLocation = this.fileStorageLocation.resolve(newFileName);
        Files.copy(file.getInputStream(), targetLocation);
        System.out.println("image path is::::"+newFileName);
        return newFileName;
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
