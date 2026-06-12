package com.example.backend.model;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

public class User {
    private Long id;
    private String username;
    private String displayName;
    private String passwordHash;
    private Set<Role> roles = new LinkedHashSet<>();
    private boolean enabled = true;
    private Instant createdAt = Instant.now();

    public User() {
    }

    public User(Long id, String username, String displayName, String passwordHash, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.passwordHash = passwordHash;
        this.roles = roles == null ? new LinkedHashSet<>() : new LinkedHashSet<>(roles);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
