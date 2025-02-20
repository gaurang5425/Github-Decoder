package com.example.github.github_Decoder.pullRequest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
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
            // 1. Fetch PR Review Comments (comments on specific lines of code)
            String reviewCommentsUrl = GITHUB_API_BASE_URL + "/repos/" + pr.getRepositoryName() + 
                                     "/pulls/" + pr.getPrId() + "/comments";
            fetchCommentsFromUrl(reviewCommentsUrl, pr, comments, entity);
            
            // 2. Fetch PR Issue Comments (general comments on the PR)
            String issueCommentsUrl = GITHUB_API_BASE_URL + "/repos/" + pr.getRepositoryName() + 
                                    "/issues/" + pr.getPrId() + "/comments";
            fetchCommentsFromUrl(issueCommentsUrl, pr, comments, entity);
            
            // 3. Fetch PR Reviews (overall review comments)
            String reviewsUrl = GITHUB_API_BASE_URL + "/repos/" + pr.getRepositoryName() + 
                              "/pulls/" + pr.getPrId() + "/reviews";
            fetchReviewsFromUrl(reviewsUrl, pr, comments, entity);
        }
        
        return comments;
    }
    
    private void fetchCommentsFromUrl(String url, PullRequestDetails pr, 
                                    List<RepositoryOwnerComment> comments, 
                                    HttpEntity<String> entity) {
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );
            
            if (response.getBody() != null) {
                for (JsonNode comment : response.getBody()) {
                    String commentAuthor = comment.get("user").get("login").asText();
                    
                    // Check if comment is from repo owner or collaborator
                    if (isRepositoryOwnerOrCollaborator(pr.getRepositoryName(), commentAuthor)) {
                        RepositoryOwnerComment ownerComment = new RepositoryOwnerComment();
                        ownerComment.setRepositoryName(pr.getRepositoryName());
                        ownerComment.setOwnerName(commentAuthor);
                        ownerComment.setComment(comment.get("body").asText());
                        comments.add(ownerComment);
                    }
                }
            }
        } catch (Exception e) {
            // Log error but continue processing other comments
      }
    }
    
    private void fetchReviewsFromUrl(String url, PullRequestDetails pr, 
                                   List<RepositoryOwnerComment> comments, 
                                   HttpEntity<String> entity) {
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );
            
            if (response.getBody() != null) {
                for (JsonNode review : response.getBody()) {
                    String reviewAuthor = review.get("user").get("login").asText();
                    JsonNode bodyNode = review.get("body");
                    
                    // Only process reviews that have a body text
                    if (bodyNode != null && !bodyNode.isNull() && 
                        isRepositoryOwnerOrCollaborator(pr.getRepositoryName(), reviewAuthor)) {
                        RepositoryOwnerComment ownerComment = new RepositoryOwnerComment();
                        ownerComment.setRepositoryName(pr.getRepositoryName());
                        ownerComment.setOwnerName(reviewAuthor);
                        ownerComment.setComment(bodyNode.asText());
                        comments.add(ownerComment);
                    }
                }
            }
        } catch (Exception e) {
            // Log error but continue processing other reviews
        }
    }
    
    private boolean isRepositoryOwnerOrCollaborator(String repoFullName, String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            // First check if user is the repository owner
            String repoUrl = GITHUB_API_BASE_URL + "/repos/" + repoFullName;
            ResponseEntity<JsonNode> repoResponse = restTemplate.exchange(
                repoUrl,
                HttpMethod.GET,
                entity,
                JsonNode.class
            );
            
            String owner = repoResponse.getBody().get("owner").get("login").asText();
            if (owner.equals(username)) {
                return true;
            }
            
            // Then check if user is a collaborator
            String collaboratorUrl = GITHUB_API_BASE_URL + "/repos/" + repoFullName + 
                                   "/collaborators/" + username;
            try {
                ResponseEntity<String> collabResponse = restTemplate.exchange(
                    collaboratorUrl,
                    HttpMethod.GET,
                    entity,
                    String.class
                );
                return collabResponse.getStatusCode() == HttpStatus.NO_CONTENT;
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                    return false;
                }
                throw e;
            }
        } catch (Exception e) {
            return false;
        }
    }
}