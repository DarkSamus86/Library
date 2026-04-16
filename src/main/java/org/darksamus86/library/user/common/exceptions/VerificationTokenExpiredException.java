package org.darksamus86.library.user.common.exceptions;

public class VerificationTokenExpiredException extends UserModuleException {
    public VerificationTokenExpiredException() {
        super("Verification token has expired");
    }
}