package com.example.github.github_Decoder.lifetime_commit;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GithubService {

    @Autowired
    private GithubUserRepository userRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${github.api.token:}")
    private String githubToken;
    
    private static final String GITHUB_API_URL = "https://api.github.com";

    public GithubUser getUserRepositoriesWithCommits(String username) {
        // Try to find user in DB
        Optional<GithubUser> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isPresent()) {
            GithubUser user = userOptional.get();
            // Check if data is recent (less than 1 day old)
            if (user.getLastUpdated().isAfter(LocalDateTime.now().minusDays(1))) {
                return user;
            }
        }
        
        // If user not found or data is stale, fetch from GitHub API
        List<Map<String, Object>> repositories = fetchUserRepositories(username);
        
        // Create or update user
        GithubUser user = userOptional.orElse(new GithubUser(username));
        user.setLastUpdated(LocalDateTime.now());
        user.getRepositories().clear(); // Remove existing repos if updating
        
        // Process each repository
        for (Map<String, Object> repoData : repositories) {
            String repoName = (String) repoData.get("name");
            Repository repository = new Repository(repoName);
            
            // Fetch commit stats for this repo
            Map<String, Integer> commitStats = fetchCommitStats(username, repoName);
            
            // Add commit stats to repo
            for (Map.Entry<String, Integer> entry : commitStats.entrySet()) {
                CommitStat stat = new CommitStat(entry.getKey(), entry.getValue());
                repository.addCommitStat(stat);
            }
            
            user.addRepository(repository);
        }
        
        // Save to database
        userRepository.save(user);
        
        return user;
    }
    
    private List<Map<String, Object>> fetchUserRepositories(String username) {
        URI uri = UriComponentsBuilder.fromUriString(GITHUB_API_URL)
                .path("/users/{username}/repos")
                .buildAndExpand(username)
                .toUri();
        
        RequestEntity<?> request = RequestEntity
                .get(uri)
                .headers(createHeaders())
                .build();
        
        try {
            return restTemplate.exchange(request, List.class).getBody();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
    
    private Map<String, Integer> fetchCommitStats(String username, String repoName) {
        Map<String, Integer> commitCountByMonth = new HashMap<>();
        
        URI uri = UriComponentsBuilder.fromUriString(GITHUB_API_URL)
                .path("/repos/{username}/{repo}/commits")
                .queryParam("author", username)
                .queryParam("per_page", 100)
                .buildAndExpand(username, repoName)
                .toUri();
        
        RequestEntity<?> request = RequestEntity
                .get(uri)
                .headers(createHeaders())
                .build();
        
        try {
            List<Map<String, Object>> commits = restTemplate.exchange(request, List.class).getBody();
            
            if (commits != null) {
                // Parse commits and group by month-year
                for (Map<String, Object> commit : commits) {
                    Map<String, Object> commitData = (Map<String, Object>) commit.get("commit");
                    Map<String, Object> committer = (Map<String, Object>) commitData.get("committer");
                    String dateStr = (String) committer.get("date");
                    
                    // Convert ISO date to YearMonth
                    ZonedDateTime dateTime = ZonedDateTime.parse(dateStr);
                    YearMonth yearMonth = YearMonth.from(dateTime);
                    String yearMonthKey = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                    
                    // Increment count for this month
                    commitCountByMonth.put(yearMonthKey, commitCountByMonth.getOrDefault(yearMonthKey, 0) + 1);
                }
            }
        } catch (Exception e) {
            // Handle exception
        }
        
        return commitCountByMonth;
    }
    
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/vnd.github.v3+json");
        if (!githubToken.isEmpty()) {
            headers.set("Authorization", "token " + githubToken);
        }
        return headers;
    }
}