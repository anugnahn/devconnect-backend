package com.devconnect.devconnectbackend.controller;

import model.Job;
import model.User;
import com.devconnect.devconnectbackend.service.ApplicationService;
import com.devconnect.devconnectbackend.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllActiveJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping
    public ResponseEntity<Job> createJob(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobService.createJob(request, user));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Job>> getMyJobs(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobService.getJobsByRecruiter(user.getId()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Job> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(jobService.updateJobStatus(id, request.get("status")));
    }

    @PostMapping("/{id}/apply")
    public ResponseEntity<?> apply(
            @PathVariable UUID id,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal User user) {
        try {
            int matchScore = request.getOrDefault("matchScore", 0);
            return ResponseEntity.ok(applicationService.apply(id, user, matchScore));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<?> getApplications(@PathVariable UUID id) {
        return ResponseEntity.ok(applicationService.getJobApplications(id));
    }
}