package org.darksamus86.library.user.common.exceptions;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserExceptionsTest {

    @Test
    void userNotFoundException_ShouldHaveCorrectMessage() {
        UserNotFoundException ex = new UserNotFoundException("123");
        assertThat(ex.getMessage()).isEqualTo("User not found with identifier: 123");
    }

    @Test
    void emailAlreadyExistsException_ShouldHaveCorrectMessage() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("test@test.com");
        assertThat(ex.getMessage()).isEqualTo("Email already registered: test@test.com");
    }

    @Test
    void usernameAlreadyExistsException_ShouldHaveCorrectMessage() {
        UsernameAlreadyExistsException ex = new UsernameAlreadyExistsException("testuser");
        assertThat(ex.getMessage()).isEqualTo("Username already taken: testuser");
    }

    @Test
    void passwordMismatchException_ShouldHaveCorrectMessage() {
        PasswordMismatchException ex = new PasswordMismatchException();
        assertThat(ex.getMessage()).isEqualTo("Current password is incorrect");
    }

    @Test
    void roleNotFoundException_ShouldHaveCorrectMessage() {
        RoleNotFoundException ex = new RoleNotFoundException("ROLE_ADMIN");
        assertThat(ex.getMessage()).isEqualTo("Role not found in database: ROLE_ADMIN");
    }

    @Test
    void userDeletionForbiddenException_ShouldHaveCorrectMessage() {
        UserDeletionForbiddenException ex = new UserDeletionForbiddenException(1L, "Has payments");
        assertThat(ex.getMessage()).isEqualTo("User deletion forbidden. userId=1, reason: Has payments");
    }

    @Test
    void accountNotVerifiedException_ShouldHaveCorrectMessage() {
        AccountNotVerifiedException ex = new AccountNotVerifiedException(1L);
        assertThat(ex.getMessage()).isEqualTo("Account is not verified. userId=1");
    }

    @Test
    void accountLockedException_ShouldHaveCorrectMessage() {
        AccountLockedException ex = new AccountLockedException(1L, "too many attempts");
        assertThat(ex.getMessage()).contains("Account locked [userId=1]");
    }

    @Test
    void invalidEmailFormatException_ShouldHaveCorrectMessage() {
        InvalidEmailFormatException ex = new InvalidEmailFormatException("bad");
        assertThat(ex.getMessage()).isEqualTo("Invalid email format: bad");
    }

    @Test
    void weakPasswordException_ShouldHaveCorrectMessage() {
        WeakPasswordException ex = new WeakPasswordException("too short");
        assertThat(ex.getMessage()).isEqualTo("Weak password: too short");
    }

    @Test
    void invalidTokenException_ShouldHaveCorrectMessage() {
        InvalidTokenException ex = new InvalidTokenException("verification");
        assertThat(ex.getMessage()).isEqualTo("Invalid or malformed verification token");
    }

    @Test
    void verificationTokenExpiredException_ShouldHaveCorrectMessage() {
        VerificationTokenExpiredException ex = new VerificationTokenExpiredException();
        assertThat(ex.getMessage()).isEqualTo("Verification token has expired");
    }

    @Test
    void optimisticLockException_ShouldHaveCorrectMessage() {
        OptimisticLockException ex = new OptimisticLockException(1L);
        assertThat(ex.getMessage()).isEqualTo("Concurrent modification detected for user. userId=1");
    }

    @Test
    void cannotRemoveLastAdminRoleException_ShouldHaveCorrectMessage() {
        CannotRemoveLastAdminRoleException ex = new CannotRemoveLastAdminRoleException();
        assertThat(ex.getMessage()).isEqualTo("Cannot remove the last administrator role from the system");
    }

    @Test
    void roleAlreadyAssignedException_ShouldHaveCorrectMessage() {
        RoleAlreadyAssignedException ex = new RoleAlreadyAssignedException(1L, "ROLE_USER");
        assertThat(ex.getMessage()).isEqualTo("Role already assigned. userId=1, role=ROLE_USER");
    }
}
