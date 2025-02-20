package com.example.github.github_Decoder.pullRequest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

@Service
public class GitHubAnalyzerService {
    
    @Value("${github.token}")
    private String githubToken;
    
    private final RestTemplate restTemplate;
    private final String GITHUB_API_BASE_URL = "https://api.github.com";
    
    public GitHubAnalyzerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    public PullRequestAnalysis analyzePullRequests(String username) throws Exception {
        PullRequestAnalysis analysis = new PullRequestAnalysis();
        analysis.setUsername(username);
        
        try {
            List<PullRequestDetails> allPRs = fetchAllPullRequests(username);
            List<RepositoryOwnerComment> ownerComments = fetchOwnerComments(allPRs);
            int forkedReposCount = fetchForkedRepositoriesCount(username);
            
            // Count PR statuses
            int accepted = 0, rejected = 0;
            for (PullRequestDetails pr : allPRs) {
                if ("ACCEPTED".equals(pr.getStatus())) accepted++;
                if ("REJECTED".equals(pr.getStatus())) rejected++;
            }
            
            analysis.setPullRequests(allPRs);
            analysis.setTotalPRs(allPRs.size());
            analysis.setAcceptedPRs(accepted);
            analysis.setRejectedPRs(rejected);
            analysis.setOwnerComments(ownerComments);
;            
        } catch (Exception e) {
            throw new Exception("Failed to analyze pull requests for user: " + username, e);
        }
        
        return analysis;
    }
    
    private List<PullRequestDetails> fetchAllPullRequests(String username) {
        List<PullRequestDetails> pullRequests = new ArrayList<>();
        
        // Create headers with authentication
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Search for all PRs by the user
        String searchUrl = GITHUB_API_BASE_URL + "/search/issues?q=author:" + username + "+type:pr";
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            searchUrl,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );
        
        JsonNode items = response.getBody().get("items");
        for (JsonNode item : items) {
            PullRequestDetails pr = new PullRequestDetails();
            pr.setPrId(item.get("number").asLong());
            pr.setTitle(item.get("title").asText());
            
            // Extract repository name from pull_request URL
            String prUrl = item.get("pull_request").get("url").asText();
            String[] urlParts = prUrl.split("/");
            pr.setRepositoryName(urlParts[4] + "/" + urlParts[5]);
            
            // Determine PR status
            String state = item.get("state").asText();
            boolean merged = fetchPRMergeStatus(prUrl);
            
            if (merged) {
                pr.setStatus("ACCEPTED");
            } else if ("closed".equals(state)) {
                pr.setStatus("REJECTED");
            } else {
                pr.setStatus("PENDING");
            }
            
            pullRequests.add(pr);
        }
        
        return pullRequests;
    }
    
    private boolean fetchPRMergeStatus(String prUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            prUrl,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );
        
        return response.getBody().get("merged").asBoolean();
    }
    
    private List<RepositoryOwnerComment> fetchOwnerComments(List<PullRequestDetails> prs) {
        List<RepositoryOwnerComment> comments = new ArrayList<>();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        for (PullRequestDetails pr : prs) {
            String commentsUrl = GITHUB_API_BASE_URL + "/repos/" + pr.getRepositoryName() + 
                               "/pulls/" + pr.getPrId() + "/comments";
            
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                commentsUrl,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );
            
            for (JsonNode comment : response.getBody()) {
                String commentAuthor = comment.get("user").get("login").asText();
                
                // Check if comment is from repo owner
                if (isRepositoryOwner(pr.getRepositoryName(), commentAuthor)) {
                    RepositoryOwnerComment ownerComment = new RepositoryOwnerComment();
                    ownerComment.setRepositoryName(pr.getRepositoryName());
                    ownerComment.setOwnerName(commentAuthor);
                    ownerComment.setComment(comment.get("body").asText());
                    comments.add(ownerComment);
                }
            }
        }
        
        return comments;
    }
    
    private boolean isRepositoryOwner(String repoFullName, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String repoUrl = GITHUB_API_BASE_URL + "/repos/" + repoFullName;
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            repoUrl,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );
        
        String owner = response.getBody().get("owner").get("login").asText();
        return owner.equals(username);
    }
    
    private int fetchForkedRepositoriesCount(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String forkedReposUrl = GITHUB_API_BASE_URL + "/users/" + username + "/repos?type=fork";
        ResponseEntity<JsonNode> response = restTemplate.exchange(
            forkedReposUrl,
            HttpMethod.GET,
            entity,
            JsonNode.class
        );
        
        return response.getBody().size();
    }
}