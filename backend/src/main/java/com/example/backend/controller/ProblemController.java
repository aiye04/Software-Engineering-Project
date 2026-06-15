package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.model.Challenge;
import com.example.backend.service.AuthService;
import com.example.backend.service.InMemoryDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    private final InMemoryDataService dataService;
    private final AuthService authService;

    public ProblemController(InMemoryDataService dataService, AuthService authService) {
        this.dataService = dataService;
        this.authService = authService;
    }

    @PutMapping("/{id}")
    public Result<Challenge> update(@PathVariable Long id, @RequestBody Challenge request) {
        return Result.ok(dataService.updateChallenge(id, challenge -> {
            challenge.setTitle(request.getTitle());
            challenge.setCategory(request.getCategory());
            challenge.setScore(request.getScore());
            challenge.setDescription(request.getDescription());
            challenge.setStatus(request.getStatus());
        }));
    }

    @PutMapping("/{id}/claim")
    public Result<Challenge> claim(@PathVariable Long id, HttpServletRequest request) {
        return Result.ok(dataService.claimChallenge(id, authService.currentUser(request).getId()));
    }

    @PutMapping("/{id}/status")
    public Result<Challenge> status(@PathVariable Long id, @RequestBody StatusRequest request) {
        return Result.ok(dataService.updateChallengeStatus(id, request.status()));
    }

    public record StatusRequest(String status) { }
}
