package com.devconnect.devconnectbackend.repository;

import model.DeveloperProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<DeveloperProfile, UUID> {
    Optional<DeveloperProfile> findByUserId(UUID userId);
}
