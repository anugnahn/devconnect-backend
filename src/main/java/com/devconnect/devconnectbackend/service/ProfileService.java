package com.devconnect.devconnectbackend.service;

import model.DeveloperProfile;
import model.User;
import com.devconnect.devconnectbackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public Optional<DeveloperProfile> getProfileByUserId(UUID userId) {
        return profileRepository.findByUserId(userId);
    }

    public DeveloperProfile saveProfile(Map<String, Object> request, User user) {
        DeveloperProfile profile = profileRepository.findByUserId(user.getId())
                .orElse(DeveloperProfile.builder().user(user).build());

        if (request.get("title") != null) profile.setTitle((String) request.get("title"));
        if (request.get("bio") != null) profile.setBio((String) request.get("bio"));
        if (request.get("location") != null) profile.setLocation((String) request.get("location"));
        if (request.get("experience") != null) profile.setExperience((String) request.get("experience"));
        if (request.get("githubUrl") != null) profile.setGithubUrl((String) request.get("githubUrl"));
        if (request.get("linkedinUrl") != null) profile.setLinkedinUrl((String) request.get("linkedinUrl"));
        if (request.get("skills") != null) profile.setSkills((List<String>) request.get("skills"));

        return profileRepository.save(profile);
    }
}