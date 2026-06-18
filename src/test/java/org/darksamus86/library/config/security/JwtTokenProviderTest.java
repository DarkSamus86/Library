package org.darksamus86.library.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.darksamus86.library.user.security.CustomUserDetails;
import org.darksamus86.library.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        String secretKey = Base64.getEncoder().encodeToString("this-is-a-very-long-secret-key-for-jwt-testing-purposes-only-1234567890".getBytes());
        ReflectionTestUtils.setField(jwtTokenProvider, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 86400000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpiration", 604800000L);
    }

    @Test
    @DisplayName("Should generate access token")
    void generateToken_ShouldReturnValidToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should generate refresh token")
    void generateRefreshToken_ShouldReturnValidToken() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtTokenProvider.generateRefreshToken(userDetails);

        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should generate token with userId for CustomUserDetails")
    void generateToken_WithCustomUserDetails_ShouldIncludeUserId() {
        User user = new User();
        user.setId(42L);
        user.setUsername("customuser");
        user.setEmail("test@test.com");
        user.setPasswordHash("hashed");
        user.setIsActive(true);
        user.setIsEmailVerified(false);
        user.setUserRoles(java.util.List.of());

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        String token = jwtTokenProvider.generateToken(customUserDetails);

        assertThat(token).isNotNull();
        Long userId = jwtTokenProvider.extractClaim(token, claims -> claims.get("userId", Long.class));
        assertThat(userId).isEqualTo(42L);
    }

    @Test
    @DisplayName("Should validate token correctly")
    void isTokenValid_WhenTokenIsValid_ShouldReturnTrue() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(jwtTokenProvider.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("Should return false when token username does not match")
    void isTokenValid_WhenUsernameMismatch_ShouldReturnFalse() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtTokenProvider.generateToken(userDetails);

        UserDetails otherUser = org.springframework.security.core.userdetails.User
                .withUsername("otheruser")
                .password("password")
                .roles("USER")
                .build();

        assertThat(jwtTokenProvider.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_ShouldReturnCorrectUsername() {
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("myuser")
                .password("password")
                .roles("USER")
                .build();

        String token = jwtTokenProvider.generateToken(userDetails);

        assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("myuser");
    }

    @Test
    @DisplayName("Should generate refresh token by username")
    void generateRefreshToken_ByUsername_ShouldReturnValidToken() {
        String token = jwtTokenProvider.generateRefreshToken("testuser");

        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.extractUsername(token)).isEqualTo("testuser");
    }
}
