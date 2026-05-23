package com.devconnect.devconnectbackend.service;

import model.Application;
import model.Job;
import model.User;
import com.devconnect.devconnectbackend.repository.ApplicationRepository;
import com.devconnect.devconnectbackend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;

    public Application apply(UUID jobId, User developer, int matchScore) {
        if (applicationRepository.existsByJobIdAndDeveloperId(jobId, developer.getId())) {
            throw new RuntimeException("Already applied to this job");
        }
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        Application application = Application.builder()
                .job(job)
                .developer(developer)
                .matchScore(matchScore)
                .build();
        return applicationRepository.save(application);
    }

    public List<Application> getMyApplications(UUID developerId) {
        return applicationRepository.findByDeveloperIdOrderByAppliedAtDesc(developerId);
    }

    public List<Application> getJobApplications(UUID jobId) {
        return applicationRepository.findByJobIdOrderByMatchScoreDesc(jobId);
    }

    public Application updateStatus(UUID applicationId, String status) {
        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(Application.ApplicationStatus.valueOf(
                status.equals("new") ? "new_" : status));
        return applicationRepository.save(app);
    }
}