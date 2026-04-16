package org.darksamus86.library.user.common.exceptions;

public class AccountNotVerifiedException extends UserModuleException {
    public AccountNotVerifiedException(Long userId) {
        super("Account is not verified. userId=" + userId);
    }
}