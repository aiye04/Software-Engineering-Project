package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.model.Challenge;
import com.example.backend.model.Competition;
import com.example.backend.service.AuthService;
import com.example.backend.service.InMemoryDataService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class CompetitionController {
    private final InMemoryDataService dataService;
    private final AuthService authService;

    public CompetitionController(InMemoryDataService dataService, AuthService authService) {
        this.dataService = dataService;
        this.authService = authService;
    }

    @GetMapping
    public Result<List<Competition>> list() { return Result.ok(dataService.listCompetitions()); }

    @PostMapping
    public Result<Competition> create(@RequestBody Competition competition) { return Result.ok(dataService.createCompetition(competition)); }

    @GetMapping("/{id}")
    public Result<Competition> detail(@PathVariable Long id) { return Result.ok(dataService.getCompetition(id)); }

    @PutMapping("/{id}")
    public Result<Competition> update(@PathVariable Long id, @RequestBody Competition request) {
        return Result.ok(dataService.updateCompetition(id, competition -> {
            competition.setTitle(request.getTitle());
            competition.setDescription(request.getDescription());
            competition.setStartTime(request.getStartTime());
            competition.setEndTime(request.getEndTime());
        }));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataService.deleteCompetition(id);
        return Result.ok();
    }

    @GetMapping("/challenges")
    public Result<List<Challenge>> listChallenges() { return Result.ok(dataService.listChallenges()); }

    @GetMapping("/{competitionId}/challenges")
    public Result<List<Challenge>> listCompetitionChallenges(@PathVariable Long competitionId) {
        return Result.ok(dataService.listChallengesByCompetition(competitionId));
    }

    @GetMapping("/{competitionId}/problems")
    public Result<List<Challenge>> listCompetitionProblems(@PathVariable Long competitionId) {
        return listCompetitionChallenges(competitionId);
    }

    @PostMapping("/{competitionId}/challenges")
    public Result<Challenge> createChallenge(@PathVariable Long competitionId, @RequestBody Challenge challenge) {
        return Result.ok(dataService.createChallenge(competitionId, challenge));
    }

    @PostMapping("/{competitionId}/problems")
    public Result<Challenge> createProblem(@PathVariable Long competitionId, @RequestBody Challenge challenge) {
        return createChallenge(competitionId, challenge);
    }

    @PutMapping("/challenges/{id}")
    public Result<Challenge> updateChallenge(@PathVariable Long id, @RequestBody Challenge request) {
        return Result.ok(dataService.updateChallenge(id, challenge -> {
            challenge.setTitle(request.getTitle());
            challenge.setCategory(request.getCategory());
            challenge.setScore(request.getScore());
            challenge.setDescription(request.getDescription());
            challenge.setStatus(request.getStatus());
        }));
    }

    @PutMapping("/challenges/{id}/claim")
    public Result<Challenge> claimChallenge(@PathVariable Long id, HttpServletRequest request) {
        return Result.ok(dataService.claimChallenge(id, authService.currentUser(request).getId()));
    }

    @PutMapping("/challenges/{id}/status")
    public Result<Challenge> updateChallengeStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return Result.ok(dataService.updateChallengeStatus(id, request.status()));
    }

    public record StatusRequest(String status) { }
}
