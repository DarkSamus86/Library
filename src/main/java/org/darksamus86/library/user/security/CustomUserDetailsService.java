package org.darksamus86.library.user.security;

import lombok.RequiredArgsConstructor;
import org.darksamus86.library.user.entity.User;
import org.darksamus86.library.user.repository.UserRepo;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Получаем сущность User (без лишних преобразований)
        User user = userRepository.findByUsernameWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 2. Преобразуем роли в формат Spring Security
        var authorities = user.getUserRoles().stream()
                .map(ur -> new SimpleGrantedAuthority(ur.getRole().getName()))
                .collect(Collectors.toList());

        // 3. Возвращаем стандартный UserDetails
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.getIsActive())
                .build();
    }
}