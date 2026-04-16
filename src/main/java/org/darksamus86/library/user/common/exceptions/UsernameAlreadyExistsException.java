package org.darksamus86.library.user.common.exceptions;

public class UsernameAlreadyExistsException extends UserModuleException {
    public UsernameAlreadyExistsException(String username) {
        super("Username already taken: " + username);
    }
}