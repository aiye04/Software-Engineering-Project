package com.example.backend.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Competition {
    private Long id;
    private String title;
    private String description;
    private Instant startTime;
    private Instant endTime;
    private List<Challenge> challenges = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }
    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public List<Challenge> getChallenges() { return challenges; }
    public void setChallenges(List<Challenge> challenges) { this.challenges = challenges; }
}
