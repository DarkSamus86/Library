package org.darksamus86.library.user.common.exceptions;


public abstract class UserModuleException extends RuntimeException {
    protected UserModuleException(String message) { super(message); }
    protected UserModuleException(String message, Throwable cause) { super(message, cause); }
}