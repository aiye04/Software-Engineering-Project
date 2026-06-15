package com.example.backend.service;

import com.example.backend.exception.ApiException;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);
    private final PasswordService passwordService;

    public UserService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @PostConstruct
    void seed() {
        register("admin", "Admin", "Admin@123456", Set.of(Role.ADMIN));
        register("teacher", "Teacher", "Teacher@123456", Set.of(Role.TEACHER));
        register("captain", "Captain", "Captain@123456", Set.of(Role.CAPTAIN));
    }

    public User register(String username, String displayName, String password, Set<Role> roles) {
        if (findByUsername(username).isPresent()) {
            throw ApiException.badRequest("username already exists");
        }
        User user = new User(ids.incrementAndGet(), username, displayName, passwordService.hash(password),
                roles == null || roles.isEmpty() ? Set.of(Role.TRAINEE) : new LinkedHashSet<>(roles));
        users.put(user.getId(), user);
        return user;
    }

    public User authenticate(String username, String password) {
        User user = findByUsername(username).orElseThrow(() -> ApiException.unauthorized("invalid credentials"));
        if (!user.isEnabled() || !passwordService.matches(password, user.getPasswordHash())) {
            throw ApiException.unauthorized("invalid credentials");
        }
        return user;
    }

    public User getById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw ApiException.notFound("user not found");
        }
        return user;
    }

    public List<User> list() {
        return new ArrayList<>(users.values());
    }

    public User update(Long id, String displayName, Set<Role> roles, Boolean enabled) {
        User user = getById(id);
        if (displayName != null && !displayName.isBlank()) {
            user.setDisplayName(displayName);
        }
        if (roles != null && !roles.isEmpty()) {
            user.setRoles(new LinkedHashSet<>(roles));
        }
        if (enabled != null) {
            user.setEnabled(enabled);
        }
        return user;
    }

    public void delete(Long id) {
        if (users.remove(id) == null) {
            throw ApiException.notFound("user not found");
        }
    }

    private Optional<User> findByUsername(String username) {
        return users.values().stream().filter(user -> user.getUsername().equalsIgnoreCase(username)).findFirst();
    }
}
