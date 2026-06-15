package com.example.backend.service;

import com.example.backend.exception.ApiException;
import com.example.backend.model.Challenge;
import com.example.backend.model.Competition;
import com.example.backend.model.Notification;
import com.example.backend.model.Submission;
import com.example.backend.model.TrainingTask;
import com.example.backend.model.WriteUp;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Service
public class InMemoryDataService {
    private final Map<Long, Competition> competitions = new LinkedHashMap<>();
    private final Map<Long, Challenge> challenges = new LinkedHashMap<>();
    private final Map<Long, WriteUp> writeUps = new LinkedHashMap<>();
    private final Map<Long, TrainingTask> tasks = new LinkedHashMap<>();
    private final Map<Long, Submission> submissions = new LinkedHashMap<>();
    private final Map<Long, Notification> notifications = new LinkedHashMap<>();
    private final AtomicLong competitionIds = new AtomicLong();
    private final AtomicLong challengeIds = new AtomicLong();
    private final AtomicLong writeUpIds = new AtomicLong();
    private final AtomicLong taskIds = new AtomicLong();
    private final AtomicLong submissionIds = new AtomicLong();
    private final AtomicLong notificationIds = new AtomicLong();

    @PostConstruct
    void seed() {
        Competition competition = createCompetition(sampleCompetition("校内选拔赛", "MVP 示例比赛"));
        createChallenge(competition.getId(), sampleChallenge("Web 入门", "web", 100));
        createChallenge(competition.getId(), sampleChallenge("Crypto 入门", "crypto", 100));
        createWriteUp(sampleWriteUp("Web 入门题解", "web", 1L));
        createTask(sampleTask("每周训练：Web 基础", "web", 4L));
        createNotification(sampleNotification("欢迎使用训练平台", "请登录后查看比赛、任务和 WriteUp。", "INFO"));
    }

    public synchronized List<Competition> listCompetitions() { return new ArrayList<>(competitions.values()); }
    public synchronized Competition createCompetition(Competition competition) {
        competition.setId(competitionIds.incrementAndGet());
        if (competition.getChallenges() == null) competition.setChallenges(new ArrayList<>());
        competitions.put(competition.getId(), competition);
        return competition;
    }
    public synchronized Competition updateCompetition(Long id, Consumer<Competition> updater) {
        Competition competition = getCompetition(id);
        updater.accept(competition);
        return competition;
    }
    public synchronized Competition getCompetition(Long id) {
        Competition competition = competitions.get(id);
        if (competition == null) throw ApiException.notFound("competition not found");
        return competition;
    }
    public synchronized void deleteCompetition(Long id) {
        if (competitions.remove(id) == null) throw ApiException.notFound("competition not found");
        challenges.values().removeIf(challenge -> id.equals(challenge.getCompetitionId()));
    }

    public synchronized Challenge createChallenge(Long competitionId, Challenge challenge) {
        Competition competition = getCompetition(competitionId);
        challenge.setId(challengeIds.incrementAndGet());
        challenge.setCompetitionId(competitionId);
        if (challenge.getStatus() == null || challenge.getStatus().isBlank()) challenge.setStatus("TODO");
        challenges.put(challenge.getId(), challenge);
        competition.getChallenges().add(challenge);
        return challenge;
    }
    public synchronized List<Challenge> listChallenges() { return new ArrayList<>(challenges.values()); }
    public synchronized List<Challenge> listChallengesByCompetition(Long competitionId) {
        getCompetition(competitionId);
        return challenges.values().stream().filter(challenge -> competitionId.equals(challenge.getCompetitionId())).toList();
    }
    public synchronized Challenge getChallenge(Long id) {
        Challenge challenge = challenges.get(id);
        if (challenge == null) throw ApiException.notFound("challenge not found");
        return challenge;
    }
    public synchronized Challenge updateChallenge(Long id, Consumer<Challenge> updater) {
        Challenge challenge = getChallenge(id);
        updater.accept(challenge);
        return challenge;
    }
    public synchronized Challenge claimChallenge(Long id, Long userId) {
        return updateChallenge(id, challenge -> {
            challenge.setClaimedBy(userId);
            challenge.setStatus("CLAIMED");
        });
    }
    public synchronized Challenge updateChallengeStatus(Long id, String status) {
        return updateChallenge(id, challenge -> {
            challenge.setStatus(status == null || status.isBlank() ? challenge.getStatus() : status);
            if ("SOLVED".equalsIgnoreCase(challenge.getStatus())) {
                challenge.setSolved(Math.max(challenge.getSolved(), 1));
            }
        });
    }

    public synchronized List<WriteUp> listWriteUps(String category) {
        return writeUps.values().stream().filter(w -> category == null || category.equalsIgnoreCase(w.getCategory())).toList();
    }
    public synchronized WriteUp createWriteUp(WriteUp writeUp) {
        writeUp.setId(writeUpIds.incrementAndGet());
        writeUps.put(writeUp.getId(), writeUp);
        return writeUp;
    }
    public synchronized WriteUp getWriteUp(Long id) {
        WriteUp writeUp = writeUps.get(id);
        if (writeUp == null) throw ApiException.notFound("writeup not found");
        return writeUp;
    }
    public synchronized WriteUp updateWriteUp(Long id, WriteUp request) {
        WriteUp writeUp = getWriteUp(id);
        writeUp.setTitle(request.getTitle());
        writeUp.setCategory(request.getCategory());
        writeUp.setContent(request.getContent());
        writeUp.setAuthorId(request.getAuthorId());
        return writeUp;
    }
    public synchronized void deleteWriteUp(Long id) {
        if (writeUps.remove(id) == null) throw ApiException.notFound("writeup not found");
    }

    public synchronized List<TrainingTask> listTasks() { return new ArrayList<>(tasks.values()); }
    public synchronized TrainingTask createTask(TrainingTask task) {
        task.setId(taskIds.incrementAndGet());
        tasks.put(task.getId(), task);
        return task;
    }
    public synchronized TrainingTask getTask(Long id) {
        TrainingTask task = tasks.get(id);
        if (task == null) throw ApiException.notFound("training task not found");
        return task;
    }
    public synchronized TrainingTask updateTask(Long id, TrainingTask request) {
        TrainingTask task = getTask(id);
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCategory(request.getCategory());
        task.setDueAt(request.getDueAt());
        task.setAssigneeId(request.getAssigneeId());
        return task;
    }
    public synchronized void deleteTask(Long id) {
        if (tasks.remove(id) == null) throw ApiException.notFound("training task not found");
        submissions.values().removeIf(submission -> id.equals(submission.getTaskId()));
    }
    public synchronized Submission submit(Long taskId, Long userId, String content) {
        if (!tasks.containsKey(taskId)) throw ApiException.notFound("training task not found");
        Submission submission = new Submission();
        submission.setId(submissionIds.incrementAndGet());
        submission.setTaskId(taskId);
        submission.setUserId(userId);
        submission.setContent(content);
        submissions.put(submission.getId(), submission);
        return submission;
    }
    public synchronized Submission scoreSubmission(Long submissionId, Integer score, String review) {
        Submission submission = submissions.get(submissionId);
        if (submission == null) throw ApiException.notFound("submission not found");
        submission.setScore(score);
        submission.setReview(review);
        submission.setStatus("REVIEWED");
        return submission;
    }
    public synchronized List<Submission> listSubmissions() { return new ArrayList<>(submissions.values()); }

    public synchronized List<Notification> listNotifications() { return new ArrayList<>(notifications.values()); }
    public synchronized Notification createNotification(Notification notification) {
        notification.setId(notificationIds.incrementAndGet());
        notifications.put(notification.getId(), notification);
        return notification;
    }
    public synchronized Notification markNotificationRead(Long id) {
        Notification notification = notifications.get(id);
        if (notification == null) throw ApiException.notFound("notification not found");
        notification.setRead(true);
        return notification;
    }

    public synchronized Map<String, Object> dashboard() {
        return Map.of(
                "userCount", 3,
                "competitionCount", competitions.size(),
                "challengeCount", challenges.size(),
                "writeUpCount", writeUps.size(),
                "trainingTaskCount", tasks.size(),
                "submissionCount", submissions.size(),
                "notificationCount", notifications.size(),
                "generatedAt", Instant.now()
        );
    }
    public synchronized List<Map<String, Object>> trend() {
        return List.of(
                Map.of("date", "周一", "writeUps", 1, "submissions", 2),
                Map.of("date", "周二", "writeUps", 2, "submissions", 3),
                Map.of("date", "周三", "writeUps", writeUps.size(), "submissions", submissions.size())
        );
    }
    public synchronized List<Map<String, Object>> distribution() {
        return List.of(
                Map.of("name", "Web", "value", challenges.values().stream().filter(c -> "web".equalsIgnoreCase(c.getCategory())).count()),
                Map.of("name", "Crypto", "value", challenges.values().stream().filter(c -> "crypto".equalsIgnoreCase(c.getCategory())).count()),
                Map.of("name", "其他", "value", challenges.values().stream().filter(c -> c.getCategory() == null || (!"web".equalsIgnoreCase(c.getCategory()) && !"crypto".equalsIgnoreCase(c.getCategory()))).count())
        );
    }

    private Competition sampleCompetition(String title, String description) {
        Competition c = new Competition();
        c.setTitle(title);
        c.setDescription(description);
        c.setStartTime(Instant.now());
        c.setEndTime(Instant.now().plusSeconds(86400));
        return c;
    }
    private Challenge sampleChallenge(String title, String category, int score) {
        Challenge c = new Challenge();
        c.setTitle(title);
        c.setCategory(category);
        c.setScore(score);
        c.setDescription("示例赛题");
        return c;
    }
    private WriteUp sampleWriteUp(String title, String category, Long authorId) {
        WriteUp w = new WriteUp();
        w.setTitle(title);
        w.setCategory(category);
        w.setAuthorId(authorId);
        w.setContent("# " + title + "\n\n记录解题思路、关键 payload 和复盘总结。");
        return w;
    }
    private TrainingTask sampleTask(String title, String category, Long assigneeId) {
        TrainingTask task = new TrainingTask();
        task.setTitle(title);
        task.setCategory(category);
        task.setDescription("完成基础题目并提交 WriteUp。");
        task.setDueAt(Instant.now().plusSeconds(604800));
        task.setAssigneeId(assigneeId);
        return task;
    }
    private Notification sampleNotification(String title, String content, String level) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setLevel(level);
        return notification;
    }
}
