package org.darksamus86.library.user.common.exceptions;

public class PasswordMismatchException extends UserModuleException {
    public PasswordMismatchException() {
        super("Current password is incorrect");
    }
}