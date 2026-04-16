package org.darksamus86.library.user.common.exceptions;

public class RoleNotFoundException extends UserModuleException {
    public RoleNotFoundException(String roleName) {
        super("Role not found in database: " + roleName);
    }
}