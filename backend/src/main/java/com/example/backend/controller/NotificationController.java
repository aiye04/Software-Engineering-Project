package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.model.Notification;
import com.example.backend.service.InMemoryDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final InMemoryDataService dataService;

    public NotificationController(InMemoryDataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public Result<List<Notification>> list() { return Result.ok(dataService.listNotifications()); }

    @PostMapping
    public Result<Notification> create(@RequestBody Notification notification) {
        return Result.ok(dataService.createNotification(notification));
    }

    @PutMapping("/{id}/read")
    public Result<Notification> read(@PathVariable Long id) {
        return Result.ok(dataService.markNotificationRead(id));
    }
}
