package com.example.backend.model;

public class Challenge {
    private Long id;
    private Long competitionId;
    private String title;
    private String category;
    private int score;
    private String description;
    private String status = "TODO";
    private Long claimedBy;
    private int solved;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getClaimedBy() { return claimedBy; }
    public void setClaimedBy(Long claimedBy) { this.claimedBy = claimedBy; }
    public int getSolved() { return solved; }
    public void setSolved(int solved) { this.solved = solved; }
}
