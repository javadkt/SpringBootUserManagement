package com.rak.usermanagement.common.util;

/**
 * @author Mohammmed Javad
 * @version 1.0
 *
 */

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
