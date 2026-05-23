package com.devconnect.devconnectbackend.controller;

import model.DeveloperProfile;
import model.Job;
import model.User;
import com.devconnect.devconnectbackend.repository.ProfileRepository;
import com.devconnect.devconnectbackend.repository.UserRepository;
import com.devconnect.devconnectbackend.service.AiMatchingService;
import com.devconnect.devconnectbackend.service.JobService;
import com.devconnect.devconnectbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/match")
@RequiredArgsConstructor
public class JobMatchingController {

    private final AiMatchingService aiMatchingService;
    private final JobService jobService;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // Get AI match scores for all jobs for current developer
    @GetMapping("/jobs")
    public ResponseEntity<?> getMatchedJobs(@AuthenticationPrincipal User user) {
        Optional<DeveloperProfile> profileOpt = profileRepository.findByUserId(user.getId());
        if (profileOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Please complete your profile first"));
        }

        DeveloperProfile profile = profileOpt.get();
        List<Job> jobs = jobService.getAllActiveJobs();

        List<Map<String, Object>> matchedJobs = jobs.stream().map(job -> {
            int score = aiMatchingService.calculateMatchScore(profile, job);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("job", job);
            result.put("matchScore", score);
            return result;
        }).sorted((a, b) ->
                (int) b.get("matchScore") - (int) a.get("matchScore")
        ).toList();

        return ResponseEntity.ok(matchedJobs);
    }

    // Trigger matching when a new job is posted — notify all matching developers
    @PostMapping("/notify/{jobId}")
    public ResponseEntity<?> notifyMatchingDevelopers(
            @PathVariable UUID jobId,
            @AuthenticationPrincipal User recruiter) {

        Job job = jobService.getJobById(jobId);
        List<User> developers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.DEVELOPER)
                .toList();

        int notified = 0;
        for (User developer : developers) {
            Optional<DeveloperProfile> profileOpt = profileRepository.findByUserId(developer.getId());
            if (profileOpt.isPresent()) {
                int score = aiMatchingService.calculateMatchScore(profileOpt.get(), job);
                if (score >= 60) {
                    notificationService.sendJobMatchNotification(
                            developer, job.getTitle(), job.getCompany(), score);
                    notified++;
                }
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "Notified " + notified + " matching developers",
                "jobId", jobId
        ));
    }
}