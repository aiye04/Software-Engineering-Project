package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.service.InMemoryDataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final InMemoryDataService dataService;

    public DashboardController(InMemoryDataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public Result<Map<String, Object>> dashboard() { return Result.ok(dataService.dashboard()); }

    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() { return Result.ok(dataService.dashboard()); }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend() { return Result.ok(dataService.trend()); }

    @GetMapping("/distribution")
    public Result<List<Map<String, Object>>> distribution() { return Result.ok(dataService.distribution()); }
}
