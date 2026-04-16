package org.darksamus86.library.user.common.exceptions;

public class WeakPasswordException extends UserModuleException {
    public WeakPasswordException(String reason) {
        super("Weak password: " + reason);
    }
}