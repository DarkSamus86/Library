package org.darksamus86.library.user.common.exceptions;

public class UserDeletionForbiddenException extends UserModuleException {
    public UserDeletionForbiddenException(Long userId, String reason) {
        super("User deletion forbidden. userId=" + userId + ", reason: " + reason);
    }
}