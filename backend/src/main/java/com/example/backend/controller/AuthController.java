package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.dto.UserView;
import com.example.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthService.LoginResponse response = authService.login(request.username(), request.password());
        return Result.ok(new AuthResponse(response.token(), UserView.from(response.user())));
    }

    @PostMapping("/register")
    public Result<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthService.LoginResponse response = authService.register(request.username(), request.displayName(), request.password());
        return Result.ok(new AuthResponse(response.token(), UserView.from(response.user())));
    }

    @GetMapping("/me")
    public Result<UserView> me(HttpServletRequest request) {
        return Result.ok(UserView.from(authService.currentUser(request)));
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) { }
    public record RegisterRequest(@NotBlank String username, @NotBlank String displayName,
                                  @Size(min = 8, max = 72) String password) { }
    public record AuthResponse(String token, UserView user) { }
}
