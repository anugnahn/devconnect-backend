package com.devconnect.devconnectbackend.service;

import model.DeveloperProfile;
import model.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiMatchingService {

    @Value("${openai.api.key}")
    private String openAiKey;

    @Value("${openai.api.url}")
    private String openAiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public int calculateMatchScore(DeveloperProfile profile, Job job) {
        try {
            // Build profile text
            String profileText = buildProfileText(profile);
            String jobText = buildJobText(job);

            // Get embeddings for both
            double[] profileEmbedding = getEmbedding(profileText);
            double[] jobEmbedding = getEmbedding(jobText);

            // Calculate cosine similarity
            double similarity = cosineSimilarity(profileEmbedding, jobEmbedding);

            // Convert to percentage (0-100)
            return (int) Math.round(similarity * 100);
        } catch (Exception e) {
            // Fallback: keyword matching score
            return calculateKeywordScore(profile, job);
        }
    }

    private String buildProfileText(DeveloperProfile profile) {
        StringBuilder sb = new StringBuilder();
        if (profile.getTitle() != null) sb.append(profile.getTitle()).append(" ");
        if (profile.getBio() != null) sb.append(profile.getBio()).append(" ");
        if (profile.getSkills() != null) sb.append(String.join(", ", profile.getSkills()));
        if (profile.getExperience() != null) sb.append(" ").append(profile.getExperience());
        return sb.toString();
    }

    private String buildJobText(Job job) {
        StringBuilder sb = new StringBuilder();
        sb.append(job.getTitle()).append(" ");
        sb.append(job.getDescription()).append(" ");
        if (job.getSkills() != null) sb.append(String.join(", ", job.getSkills()));
        if (job.getExperience() != null) sb.append(" ").append(job.getExperience());
        return sb.toString();
    }

    private double[] getEmbedding(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "text-embedding-3-small");
        body.put("input", text);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                openAiUrl + "/embeddings",
                HttpMethod.POST,
                request,
                Map.class
        );

        List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
        List<Double> embedding = (List<Double>) data.get(0).get("embedding");
        return embedding.stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double cosineSimilarity(double[] a, double[] b) {
        double dotProduct = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // Fallback keyword matching when OpenAI is not available
    private int calculateKeywordScore(DeveloperProfile profile, Job job) {
        if (profile.getSkills() == null || job.getSkills() == null) return 50;
        List<String> profileSkills = profile.getSkills().stream()
                .map(String::toLowerCase).toList();
        List<String> jobSkills = job.getSkills().stream()
                .map(String::toLowerCase).toList();
        long matches = jobSkills.stream()
                .filter(profileSkills::contains).count();
        double score = (double) matches / jobSkills.size();
        return (int) Math.round(50 + score * 45);
    }
}