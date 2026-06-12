package com.example.backend.dto;

import com.example.backend.model.Role;
import com.example.backend.model.User;

import java.time.Instant;
import java.util.Set;

public record UserView(Long id, String username, String displayName, Set<Role> roles, boolean enabled, Instant createdAt) {
    public static UserView from(User user) {
        return new UserView(user.getId(), user.getUsername(), user.getDisplayName(), user.getRoles(), user.isEnabled(), user.getCreatedAt());
    }
}
