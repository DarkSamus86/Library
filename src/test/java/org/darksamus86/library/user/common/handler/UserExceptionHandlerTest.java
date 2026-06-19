package org.darksamus86.library.user.common.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.darksamus86.library.user.common.exceptions.*;
import org.darksamus86.library.user.dto.response.UserErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserExceptionHandlerTest {

    private final UserExceptionHandler handler = new UserExceptionHandler();

    private HttpServletRequest mockRequest(String uri) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        return request;
    }

    @Test
    @DisplayName("Should return 404 for UserNotFoundException")
    void handleNotFound_ShouldReturn404() {
        UserNotFoundException ex = new UserNotFoundException("123");
        HttpServletRequest request = mockRequest("/user/123");

        ResponseEntity<UserErrorResponse> response = handler.handleNotFound(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(404);
        assertThat(response.getBody().message()).contains("123");
    }

    @Test
    @DisplayName("Should return 409 for EmailAlreadyExistsException")
    void handleConflict_EmailAlreadyExists_ShouldReturn409() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("test@test.com");
        HttpServletRequest request = mockRequest("/user/register");

        ResponseEntity<UserErrorResponse> response = handler.handleConflict(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should return 409 for UsernameAlreadyExistsException")
    void handleConflict_UsernameAlreadyExists_ShouldReturn409() {
        UsernameAlreadyExistsException ex = new UsernameAlreadyExistsException("testuser");
        HttpServletRequest request = mockRequest("/user/register");

        ResponseEntity<UserErrorResponse> response = handler.handleConflict(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should return 409 for RoleAlreadyAssignedException")
    void handleConflict_RoleAlreadyAssigned_ShouldReturn409() {
        RoleAlreadyAssignedException ex = new RoleAlreadyAssignedException(1L, "ROLE_USER");
        HttpServletRequest request = mockRequest("/user/roles");

        ResponseEntity<UserErrorResponse> response = handler.handleConflict(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
    }

    @Test
    @DisplayName("Should return 400 for InvalidEmailFormatException")
    void handleBadRequest_InvalidEmail_ShouldReturn400() {
        InvalidEmailFormatException ex = new InvalidEmailFormatException("bad-email");
        HttpServletRequest request = mockRequest("/user/register");

        ResponseEntity<UserErrorResponse> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return 400 for WeakPasswordException")
    void handleBadRequest_WeakPassword_ShouldReturn400() {
        WeakPasswordException ex = new WeakPasswordException("too short");
        HttpServletRequest request = mockRequest("/user/register");

        ResponseEntity<UserErrorResponse> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return 400 for InvalidTokenException")
    void handleBadRequest_InvalidToken_ShouldReturn400() {
        InvalidTokenException ex = new InvalidTokenException("verification");
        HttpServletRequest request = mockRequest("/user/verify");

        ResponseEntity<UserErrorResponse> response = handler.handleBadRequest(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().status()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return 401 for PasswordMismatchException")
    void handleUnauthorized_ShouldReturn401() {
        PasswordMismatchException ex = new PasswordMismatchException();
        HttpServletRequest request = mockRequest("/user/update");

        ResponseEntity<UserErrorResponse> response = handler.handleUnauthorized(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().status()).isEqualTo(401);
    }

    @Test
    @DisplayName("Should return 403 for AccountNotVerifiedException")
    void handleForbidden_AccountNotVerified_ShouldReturn403() {
        AccountNotVerifiedException ex = new AccountNotVerifiedException(1L);
        HttpServletRequest request = mockRequest("/user/login");

        ResponseEntity<UserErrorResponse> response = handler.handleForbidden(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should return 403 for AccountLockedException")
    void handleForbidden_AccountLocked_ShouldReturn403() {
        AccountLockedException ex = new AccountLockedException(1L, "too many attempts");
        HttpServletRequest request = mockRequest("/user/login");

        ResponseEntity<UserErrorResponse> response = handler.handleForbidden(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should return 403 for CannotRemoveLastAdminRoleException")
    void handleForbidden_CannotRemoveLastAdmin_ShouldReturn403() {
        CannotRemoveLastAdminRoleException ex = new CannotRemoveLastAdminRoleException();
        HttpServletRequest request = mockRequest("/admin/roles");

        ResponseEntity<UserErrorResponse> response = handler.handleForbidden(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should return 403 for UserDeletionForbiddenException")
    void handleForbidden_UserDeletionForbidden_ShouldReturn403() {
        UserDeletionForbiddenException ex = new UserDeletionForbiddenException(1L, "Has payment methods");
        HttpServletRequest request = mockRequest("/user/1");

        ResponseEntity<UserErrorResponse> response = handler.handleForbidden(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().status()).isEqualTo(403);
    }

    @Test
    @DisplayName("Should return 500 for RoleNotFoundException")
    void handleServerRoleError_ShouldReturn500() {
        RoleNotFoundException ex = new RoleNotFoundException("ROLE_USER");
        HttpServletRequest request = mockRequest("/user/register");

        ResponseEntity<UserErrorResponse> response = handler.handleServerRoleError(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().status()).isEqualTo(500);
    }

    @Test
    @DisplayName("Should return 410 for VerificationTokenExpiredException")
    void handleGone_ShouldReturn410() {
        VerificationTokenExpiredException ex = new VerificationTokenExpiredException();
        HttpServletRequest request = mockRequest("/user/verify");

        ResponseEntity<UserErrorResponse> response = handler.handleGone(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GONE);
        assertThat(response.getBody().status()).isEqualTo(410);
    }

    @Test
    @DisplayName("Should return 409 for OptimisticLockException")
    void handleConcurrency_ShouldReturn409() {
        OptimisticLockException ex = new OptimisticLockException(1L);
        HttpServletRequest request = mockRequest("/user/update");

        ResponseEntity<UserErrorResponse> response = handler.handleConcurrency(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().status()).isEqualTo(409);
    }
}
