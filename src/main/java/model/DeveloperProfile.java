package model;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import model.User;
@Entity
@Table(name = "developer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeveloperProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String location;
    private String experience;

    @Column(name = "github_url")
    private String githubUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(columnDefinition = "TEXT")
    private String embedding;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "developer_skills",
            joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill")
    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}
