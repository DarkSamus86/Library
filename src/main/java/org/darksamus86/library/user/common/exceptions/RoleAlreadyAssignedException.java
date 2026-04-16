package org.darksamus86.library.user.common.exceptions;

public class RoleAlreadyAssignedException extends UserModuleException {
    public RoleAlreadyAssignedException(Long userId, String role) {
        super("Role already assigned. userId=" + userId + ", role=" + role);
    }
}