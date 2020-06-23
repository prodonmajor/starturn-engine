/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.multipart;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author akinw
 */
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;
    private String profilePicDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getProfilePicDir() {
        return profilePicDir;
    }

    public void setProfilePicDir(String profilePicDir) {
        this.profilePicDir = profilePicDir;
    }
    
}
