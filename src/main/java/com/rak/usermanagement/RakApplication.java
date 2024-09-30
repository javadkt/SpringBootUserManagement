package com.rak.usermanagement;

import com.rak.usermanagement.common.config.FileStorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @version 1.0
 *
 */

@SpringBootApplication
@EnableConfigurationProperties({FileStorageProperties.class})
public class RakApplication {

    public static void main(String[] args) {
        SpringApplication.run(RakApplication.class, args);
    }

}


