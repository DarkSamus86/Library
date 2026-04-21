package org.darksamus86.library.user.security;

import lombok.Getter;
import org.darksamus86.library.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Преобразуем роли из БД в формат Spring Security
        return user.getUserRoles().stream()
                .map(ur -> new SimpleGrantedAuthority(ur.getRole().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // ✅ Дополнительные методы для удобства
    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    // ✅ Остальные методы UserDetails
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return user.getIsActive(); }
}