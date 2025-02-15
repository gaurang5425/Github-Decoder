// GitHubRepositoryService.java
package com.example.github.github_Decoder.UserName;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;

@Service
public class GitHubRepositoryService {
    private final RestTemplate restTemplate;
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    
    @Value("${github.token:}")
    private String githubToken;
    
    public GitHubRepositoryService() {
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
    
    public List<GitHubRepository> getUserRepositories(String username) {
        // First get user info to get avatar URL
        String userUrl = String.format("%s/users/%s", GITHUB_API_BASE_URL, username);
        ResponseEntity<UserResponse> userResponse = restTemplate.exchange(
            userUrl,
            HttpMethod.GET,
            createHeaders(),
            UserResponse.class
        );
        
        String avatarUrl = userResponse.getBody().getAvatarUrl();
        
        // Get repositories
        String reposUrl = String.format("%s/users/%s/repos", GITHUB_API_BASE_URL, username);
        ResponseEntity<RepoResponse[]> reposResponse = restTemplate.exchange(
            reposUrl,
            HttpMethod.GET,
            createHeaders(),
            RepoResponse[].class
        );
        
        List<GitHubRepository> repositories = new ArrayList<>();
        
        // For each repository, get languages and set avatar URL
        for (RepoResponse repo : reposResponse.getBody()) {
            String languagesUrl = String.format("%s/repos/%s/%s/languages", GITHUB_API_BASE_URL, username, repo.getName());
            ResponseEntity<LanguagesResponse> languagesResponse = restTemplate.exchange(
                languagesUrl,
                HttpMethod.GET,
                createHeaders(),
                LanguagesResponse.class
            );
            
            // Convert language map keys to list
            List<String> languagesList = new ArrayList<>(languagesResponse.getBody().keySet());
            
            // Create new repository object with all required information
            GitHubRepository repository = new GitHubRepository(
                repo.getName(),
                repo.getHtmlUrl(),
                repo.getLanguage(),
                languagesList,
                userResponse.getBody().getFollowers(),
                userResponse.getBody().getLogin(),
                avatarUrl,
                repo.getWatchers(),
                repo.getStargazers_count()
            );
            
            repositories.add(repository);
        }
        
        return repositories;
    }
    
    // Helper class for user response
    private static class UserResponse {
        @JsonProperty("avatar_url")
        private String avatarUrl;
        
        private int followers;
        private String login; 
        public int getFollowers() {
			return followers;
		}
		public String getLogin() {
			return login;
		}
		public String getAvatarUrl() {
            return avatarUrl;
        }
    }
    
    // Helper class for initial repository response
    private static class RepoResponse {
        private String name;
        
        @JsonProperty("html_url")
        private String htmlUrl;
        private String  language;
        private int watchers;
        private int stargazers_count;
        
        
        public int getStargazers_count() {
			return stargazers_count;
		}

		public int getWatchers() {
			return watchers;
		}

		public String getLanguage() {
			return language;
		}

		public void setLanguage(String language) {
			this.language = language;
		}

		public String getName() {
            return name;
        }
        
        public String getHtmlUrl() {
            return htmlUrl;
        }
    }
    
    // Helper class for languages response
    private static class LanguagesResponse extends java.util.HashMap<String, Integer> {
        private static final long serialVersionUID = 1L;
    }
}