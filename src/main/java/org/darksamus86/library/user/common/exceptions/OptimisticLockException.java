package org.darksamus86.library.user.common.exceptions;

public class OptimisticLockException extends UserModuleException {
    public OptimisticLockException(Long userId) {
        super("Concurrent modification detected for user. userId=" + userId);
    }
}