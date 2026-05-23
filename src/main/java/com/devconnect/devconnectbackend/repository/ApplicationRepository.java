package com.devconnect.devconnectbackend.repository;

import model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByDeveloperIdOrderByAppliedAtDesc(UUID developerId);
    List<Application> findByJobIdOrderByMatchScoreDesc(UUID jobId);
    Optional<Application> findByJobIdAndDeveloperId(UUID jobId, UUID developerId);
    boolean existsByJobIdAndDeveloperId(UUID jobId, UUID developerId);
}