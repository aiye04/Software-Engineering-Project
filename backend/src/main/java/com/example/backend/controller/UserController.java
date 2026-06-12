package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.dto.UserView;
import com.example.backend.model.Role;
import com.example.backend.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<List<UserView>> list() {
        return Result.ok(userService.list().stream().map(UserView::from).toList());
    }

    @GetMapping("/{id}")
    public Result<UserView> detail(@PathVariable Long id) {
        return Result.ok(UserView.from(userService.getById(id)));
    }

    @PostMapping
    public Result<UserView> create(@Valid @RequestBody CreateUserRequest request) {
        return Result.ok(UserView.from(userService.register(request.username(), request.displayName(), request.password(), request.roles())));
    }

    @PatchMapping("/{id}")
    public Result<UserView> update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return Result.ok(UserView.from(userService.update(id, request.displayName(), request.roles(), request.enabled())));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    public record CreateUserRequest(@NotBlank String username, @NotBlank String displayName,
                                    @Size(min = 8, max = 72) String password, Set<Role> roles) { }
    public record UpdateUserRequest(String displayName, Set<Role> roles, Boolean enabled) { }
}
