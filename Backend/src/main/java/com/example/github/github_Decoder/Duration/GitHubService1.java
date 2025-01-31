package com.example.github.github_Decoder.Duration;


import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GitHubService1 {
    private final RestTemplate restTemplate;
    private final String GITHUB_API_URL = "https://api.github.com";

    @Value("${github.token:}")
    private String githubToken;

    public GitHubService1() {
        this.restTemplate = new RestTemplate();
    }

    private HttpEntity<String> createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");
        if (githubToken != null && !githubToken.isEmpty()) {
            headers.set("Authorization", "Bearer " + githubToken);
        }
        return new HttpEntity<>(headers);
    }

    public ProjectDuration getProjectDuration(String owner, String repo) throws ParseException, JsonMappingException, JsonProcessingException{

        HttpEntity<String> entity = createHeaders();



        int totalCommits = getTotalCommits(owner, repo, entity);



        String firstCommitDate = getFirstCommitDate(owner, repo, totalCommits, entity);
        String lastCommitDate = getLastCommitDate(owner, repo, entity);

        return new ProjectDuration(firstCommitDate, lastCommitDate, totalCommits);

    }

    private int getTotalCommits(String owner, String repo, HttpEntity<String> entity) {
        String contributorsUrl = String.format("http://localhost:8087/contributors?owner=%s&repo=%s", owner, repo);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    contributorsUrl, HttpMethod.GET, entity, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> contributors = objectMapper.readValue(response.getBody(), new TypeReference<List<Map<String, Object>>>() {});

            // Sum up total contributions from all contributors
            int totalCommits = contributors.stream()
                    .mapToInt(contributor -> (Integer) contributor.get("contributions"))
                    .sum();
            System.out.println(totalCommits);
            return totalCommits;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing contributors response", e);
        }
    }
    private String getFirstCommitDate(String owner, String repo, int totalCommits, HttpEntity<String> entity) throws JsonMappingException, JsonProcessingException {
        String firstCommitUrl = String.format("%s/repos/%s/%s/commits?per_page=1&page=%d",
                GITHUB_API_URL, owner, repo, totalCommits);

        ResponseEntity<String> response = restTemplate.exchange(
                firstCommitUrl, HttpMethod.GET, entity, String.class);

        String responseBody = response.getBody();

        // Use ObjectMapper to parse the response body
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> commits = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});

        if (commits.isEmpty()) {
            throw new IllegalStateException("No commits found in the response.");
        }

        // Access nested fields
        Map<String, Object> firstCommit = commits.get(0);
        Map<String, Object> commit = (Map<String, Object>) firstCommit.get("commit");
        Map<String, Object> committer = (Map<String, Object>) commit.get("committer");

        // Extract and return the date
        return (String) committer.get("date");
    }

    private String getLastCommitDate(String owner, String repo, HttpEntity<String> entity) throws JsonMappingException, JsonProcessingException {
        String lastCommitUrl = String.format("%s/repos/%s/%s/commits?per_page=1",
                GITHUB_API_URL, owner, repo);

        ResponseEntity<String> response = restTemplate.exchange(
                lastCommitUrl, HttpMethod.GET, entity, String.class);

        String responseBody = response.getBody();

        // Use ObjectMapper to parse the response body
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> commits = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});

        if (commits.isEmpty()) {
            throw new IllegalStateException("No commits found in the response.");
        }

        // Access nested fields
        Map<String, Object> firstCommit = commits.get(0);
        Map<String, Object> commit = (Map<String, Object>) firstCommit.get("commit");
        Map<String, Object> committer = (Map<String, Object>) commit.get("committer");

        // Extract and return the date
        return (String) committer.get("date");
        }


}