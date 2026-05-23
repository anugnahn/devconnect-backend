package com.devconnect.devconnectbackend.service;

import model.Job;
import model.User;
import com.devconnect.devconnectbackend.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;

    @Cacheable(value = "jobs")
    public List<Job> getAllActiveJobs() {
        return jobRepository.findByStatusOrderByCreatedAtDesc(Job.JobStatus.active);
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public Job createJob(Map<String, Object> request, User recruiter) {
        Job job = Job.builder()
                .title((String) request.get("title"))
                .company((String) request.get("company"))
                .location((String) request.get("location"))
                .type((String) request.get("type"))
                .salary((String) request.get("salary"))
                .experience((String) request.get("experience"))
                .description((String) request.get("description"))
                .skills((List<String>) request.get("skills"))
                .recruiter(recruiter)
                .build();
        return jobRepository.save(job);
    }

    public Job getJobById(UUID id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    public List<Job> getJobsByRecruiter(UUID recruiterId) {
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId);
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public Job updateJobStatus(UUID jobId, String status) {
        Job job = getJobById(jobId);
        job.setStatus(Job.JobStatus.valueOf(status));
        return jobRepository.save(job);
    }
}