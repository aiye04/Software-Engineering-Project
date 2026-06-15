package com.example.backend.controller;

import com.example.backend.dto.Result;
import com.example.backend.model.WriteUp;
import com.example.backend.service.InMemoryDataService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/writeups")
public class WriteUpController {
    private final InMemoryDataService dataService;

    public WriteUpController(InMemoryDataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping
    public Result<List<WriteUp>> list(@RequestParam(required = false) String category) {
        return Result.ok(dataService.listWriteUps(category));
    }

    @PostMapping
    public Result<WriteUp> create(@RequestBody WriteUp writeUp) {
        return Result.ok(dataService.createWriteUp(writeUp));
    }

    @GetMapping("/{id}")
    public Result<WriteUp> detail(@PathVariable Long id) {
        return Result.ok(dataService.getWriteUp(id));
    }

    @PutMapping("/{id}")
    public Result<WriteUp> update(@PathVariable Long id, @RequestBody WriteUp writeUp) {
        return Result.ok(dataService.updateWriteUp(id, writeUp));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        dataService.deleteWriteUp(id);
        return Result.ok();
    }
}
