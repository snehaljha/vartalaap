package com.gazab.chatServer.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {
    
    public UsernameAlreadyExistsException(String username) {
        super("username " + username + " already taken");
    }
}
