package org.darksamus86.library.user.common.exceptions;

public class EmailAlreadyExistsException extends UserModuleException {
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}