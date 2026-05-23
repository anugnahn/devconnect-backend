
package com.devconnect.devconnectbackend.repository;

import model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByStatusOrderByCreatedAtDesc(Job.JobStatus status);
    List<Job> findByRecruiterIdOrderByCreatedAtDesc(UUID recruiterId);
}