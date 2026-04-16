package org.darksamus86.library.user.common.exceptions;

public class InvalidTokenException extends UserModuleException {
    public InvalidTokenException(String tokenType) {
        super("Invalid or malformed " + tokenType + " token");
    }
}