package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.model.Submission;
import com.example.backend.model.TrainingTask;
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
@RequestMapping("/api/training")
public class TrainingController {
    private final InMemoryDataService dataService;
    private final AuthService authService;

    public TrainingController(InMemoryDataService dataService, AuthService authService) {
        this.dataService = dataService;
        this.authService = authService;
    }

    @GetMapping("/tasks")
    public Result<List<TrainingTask>> listTasks() { return Result.ok(dataService.listTasks()); }

    @PostMapping("/tasks")
    public Result<TrainingTask> createTask(@RequestBody TrainingTask task) { return Result.ok(dataService.createTask(task)); }

    @GetMapping("/tasks/{taskId}")
    public Result<TrainingTask> detail(@PathVariable Long taskId) { return Result.ok(dataService.getTask(taskId)); }

    @PutMapping("/tasks/{taskId}")
    public Result<TrainingTask> update(@PathVariable Long taskId, @RequestBody TrainingTask task) {
        return Result.ok(dataService.updateTask(taskId, task));
    }

    @DeleteMapping("/tasks/{taskId}")
    public Result<Void> delete(@PathVariable Long taskId) {
        dataService.deleteTask(taskId);
        return Result.ok();
    }

    @PostMapping("/tasks/{taskId}/submissions")
    public Result<Submission> submit(@PathVariable Long taskId, @RequestBody SubmitRequest submitRequest, HttpServletRequest request) {
        Long userId = authService.currentUser(request).getId();
        return Result.ok(dataService.submit(taskId, userId, submitRequest.content()));
    }

    @GetMapping("/submissions")
    public Result<List<Submission>> listSubmissions() { return Result.ok(dataService.listSubmissions()); }

    @PutMapping("/submissions/{submissionId}/score")
    public Result<Submission> score(@PathVariable Long submissionId, @RequestBody ScoreRequest scoreRequest) {
        return Result.ok(dataService.scoreSubmission(submissionId, scoreRequest.score(), scoreRequest.review()));
    }

    public record SubmitRequest(String content) { }
    public record ScoreRequest(Integer score, String review) { }
}
