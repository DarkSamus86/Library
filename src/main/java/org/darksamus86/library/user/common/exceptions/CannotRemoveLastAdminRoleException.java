package org.darksamus86.library.user.common.exceptions;

public class CannotRemoveLastAdminRoleException extends UserModuleException {
    public CannotRemoveLastAdminRoleException() {
        super("Cannot remove the last administrator role from the system");
    }
}