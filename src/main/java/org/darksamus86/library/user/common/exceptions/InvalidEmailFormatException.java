package org.darksamus86.library.user.common.exceptions;

public class InvalidEmailFormatException extends UserModuleException {
    public InvalidEmailFormatException(String email) {
        super("Invalid email format: " + email);
    }
}