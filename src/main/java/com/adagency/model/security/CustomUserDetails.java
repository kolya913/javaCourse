package com.adagency.model.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@SuppressWarnings("serial")
public class CustomUserDetails extends User {
    private final Long userId;

    public CustomUserDetails(String username, String password, Collection<SimpleGrantedAuthority> authorities, Long userId) {
        super(username, password, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}

