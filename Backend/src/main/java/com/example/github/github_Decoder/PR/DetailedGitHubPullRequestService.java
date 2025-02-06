
package com.example.github.github_Decoder.PR;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DetailedGitHubPullRequestService {
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    @Autowired
    private final RestTemplate restTemplate;
    @Value("${github.token}")
    private String githubToken;
    int totalAcceptedPullRequest=0;


    public DetailedGitHubPullRequestService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public DetailedPullRequestStats getComprehensivePullRequestStats(String username) {
        // Fetch user's repositories
        String reposUrl = GITHUB_API_BASE_URL + "/users/" + username + "/repos";
        ResponseEntity<Repository[]> reposResponse = restTemplate.exchange(
            reposUrl, 
            HttpMethod.GET, 
            new HttpEntity<>(createHeaders(githubToken)), 
            Repository[].class
        );
        Repository[] repositories = reposResponse.getBody();
 
        if (repositories == null) {
            throw new RuntimeException("Failed to fetch repositories");
        }
        
        // Process pull requests for each repository
        List<RepositoryPullRequestDetail> repositoryDetails = new ArrayList<>();
        int totalPullRequests = 0;
        for (Repository repo : repositories) {
            String pullRequestsUrl = String.format("%s/repos/%s/%s/pulls?state=all", 
                GITHUB_API_BASE_URL, username, repo.getName());
            
            ResponseEntity<PullRequest[]> pullRequestsResponse = restTemplate.exchange(
                pullRequestsUrl, 
                HttpMethod.GET, 
                new HttpEntity<>(createHeaders(githubToken)), 
                PullRequest[].class
            );
            PullRequest[] pulls = pullRequestsResponse.getBody();

            if (pulls != null) {
                RepositoryPullRequestDetail repoDetail = processRepositoryPullRequests(username,repo, pulls, githubToken);
                repositoryDetails.add(repoDetail);
                totalPullRequests += repoDetail.getTotalPullRequests();
            }
        }

        return new DetailedPullRequestStats(
            username, 
            repositories.length, 
            totalPullRequests, 
            totalAcceptedPullRequest,
            repositoryDetails
        );
    }

    private RepositoryPullRequestDetail processRepositoryPullRequests(String username,Repository repo, PullRequest[] pulls, String accessToken) {
        List<RepositoryPullRequestDetail.PullRequestInfo> prInfos = new ArrayList<>();
                String repo_Link="https://github.com/"+username+"/"+repo.getName();
        for (PullRequest pr : pulls) {
            // Fetch PR reviews
            String reviewsUrl = pr.getUrl() + "/reviews";
            ResponseEntity<Review[]> reviewsResponse = restTemplate.exchange(
                reviewsUrl,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessToken)),
                Review[].class
            );
            Review[] reviews = reviewsResponse.getBody();

            RepositoryPullRequestDetail.PullRequestInfo prInfo = new RepositoryPullRequestDetail.PullRequestInfo();
            prInfo.setTitle(pr.getTitle());
            prInfo.setState(pr.getState());
            
            // Determine acceptance based on reviews
            boolean accepted = reviews != null && 
                Arrays.stream(reviews)
                    .anyMatch(review -> "APPROVED".equals(review.getState()));
            prInfo.setAccepted(accepted);
                     if(accepted==true)
                    	 totalAcceptedPullRequest++;
            // Get maintainer comment
            String maintainerComment = findMaintainerComment(reviews);
            prInfo.setMaintainerComment(maintainerComment);
         
            prInfos.add(prInfo);
        }

        return new RepositoryPullRequestDetail(
                repo.getName(), 
                repo_Link,
                prInfos.size(), 
                prInfos
            );
    }

    private String findMaintainerComment(Review[] reviews) {
        if (reviews == null || reviews.length == 0) {
            return "";
        }
        return Arrays.stream(reviews)
            .filter(review -> review.getBody() != null && !review.getBody().isEmpty())
            .findFirst()
            .map(Review::getBody)
            .orElse("");
    }

    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);  // Changed from "Bearer " to "token "
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }
}