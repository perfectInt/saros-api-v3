package ru.saros.sarosapiv3.api.security.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    ADMINISTRATOR("ADMINISTRATOR"),
    MODERATOR("MODERATOR"),
    USER("USER");

    private final String role;

    @Override
    public String getAuthority() {
        return role;
    }
}
