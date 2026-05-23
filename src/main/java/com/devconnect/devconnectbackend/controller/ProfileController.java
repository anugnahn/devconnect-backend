package com.devconnect.devconnectbackend.controller;

import model.DeveloperProfile;
import model.User;
import com.devconnect.devconnectbackend.service.ApplicationService;
import com.devconnect.devconnectbackend.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal User user) {
        Optional<DeveloperProfile> profile = profileService.getProfileByUserId(user.getId());
        return profile.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DeveloperProfile> saveProfile(
            @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(profileService.saveProfile(request, user));
    }

    @GetMapping("/applications")
    public ResponseEntity<?> getMyApplications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(applicationService.getMyApplications(user.getId()));
    }
}