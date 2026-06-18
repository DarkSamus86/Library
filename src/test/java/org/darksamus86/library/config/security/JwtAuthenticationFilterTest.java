package org.darksamus86.library.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Test
    @DisplayName("Should skip filter when no Authorization header")
    void doFilterInternal_WhenNoAuthHeader_ShouldContinueChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Should skip filter when Authorization header does not start with Bearer")
    void doFilterInternal_WhenInvalidAuthHeader_ShouldContinueChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic something");

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider, userDetailsService);
    }

    @Test
    @DisplayName("Should authenticate when valid token provided")
    void doFilterInternal_WhenValidToken_ShouldAuthenticate() throws Exception {
        String jwt = "valid.jwt.token";
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtTokenProvider.extractUsername(jwt)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtTokenProvider.isTokenValid(jwt, userDetails)).thenReturn(true);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider).extractUsername(jwt);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(jwtTokenProvider).isTokenValid(jwt, userDetails);
    }

    @Test
    @DisplayName("Should continue chain when token is invalid")
    void doFilterInternal_WhenInvalidToken_ShouldContinueChain() throws Exception {
        String jwt = "invalid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtTokenProvider.extractUsername(jwt)).thenThrow(new RuntimeException("Invalid token"));

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Should continue chain when username is null")
    void doFilterInternal_WhenNullUsername_ShouldContinueChain() throws Exception {
        String jwt = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtTokenProvider.extractUsername(jwt)).thenReturn(null);

        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }

    @Test
    void setUp() {
        SecurityContextHolder.clearContext();
    }
}
