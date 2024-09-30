package com.rak.usermanagement.common.config;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Mohammmed Javad
 * @version 1.0
 *
 */

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }
}
