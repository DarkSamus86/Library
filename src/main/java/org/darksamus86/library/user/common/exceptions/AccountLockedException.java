package org.darksamus86.library.user.common.exceptions;

public class AccountLockedException extends UserModuleException {
    public AccountLockedException(Long userId, String reason) {
        super(String.format("Account locked [userId=%d]. Reason: %s", userId, reason));
    }
}