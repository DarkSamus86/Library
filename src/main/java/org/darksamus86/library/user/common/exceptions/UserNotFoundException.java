package org.darksamus86.library.user.common.exceptions;

public class UserNotFoundException extends UserModuleException {
    public UserNotFoundException(String identifier) {
        super("User not found with identifier: " + identifier);
    }
}