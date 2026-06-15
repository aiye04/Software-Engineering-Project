package com.example.backend.service;

import com.example.backend.exception.ApiException;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {
    private final UserService userService;
    private final TokenService tokenService;

    public AuthService(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    public LoginResponse login(String username, String password) {
        User user = userService.authenticate(username, password);
        return new LoginResponse(tokenService.createToken(user), user);
    }

    public LoginResponse register(String username, String displayName, String password) {
        User user = userService.register(username, displayName, password, Set.of(Role.TRAINEE));
        return new LoginResponse(tokenService.createToken(user), user);
    }

    public User currentUser(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw ApiException.unauthorized("missing bearer token");
        }
        return userService.getById(tokenService.parseUserId(header.substring(7)));
    }

    public record LoginResponse(String token, User user) { }
}
