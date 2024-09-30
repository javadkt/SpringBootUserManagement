package com.rak.usermanagement.common.model;

import java.io.Serializable;

/**
 * @author Mohammmed Javad
 * @version 1.0

 */

public class AuthenticationResponse implements Serializable {

    private final User user;
    private final String authToken;

    public AuthenticationResponse(User user, String authToken) {
        this.user = user;
        this.authToken = authToken;
    }

    public User getUser() {
        return user;
    }

    public String getAuthToken() {
        return authToken;
    }
}
