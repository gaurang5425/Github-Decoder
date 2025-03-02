package com.example.github.github_Decoder.FileByContributer;


import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import org.springframework.http.HttpHeaders;

@Service
public class ContributorLanguageService {
    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;
    

    @Autowired
    public ContributorLanguageService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Map<String, Object> analyzeContributorLanguages(String owner, String repo, ContributerLanguageRepository clr) {
        try {
            String contributorsUrl = String.format("https://api.github.com/repos/%s/%s/contributors", owner, repo);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + githubToken);
            headers.set("Accept", "application/vnd.github.v3+json");
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            // First, get basic contributor information
            ResponseEntity<List<Map<String, Object>>> contributorsResponse = restTemplate.exchange(
                contributorsUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (contributorsResponse.getBody() == null || contributorsResponse.getBody().isEmpty()) {
                throw new RuntimeException("No contributors found for repository: " + owner + "/" + repo);
            }

            Map<String, Map<String, Integer>> contributorLanguages = new HashMap<>();
            Map<String, Integer> totalLanguageContributions = new HashMap<>();

            // Process each contributor
            for (Map<String, Object> contributor : contributorsResponse.getBody()) {
                String username = (String) contributor.get("login");
                if (username == null) continue;

                // Get commits for this contributor
                processContributorCommits(owner, repo, username, entity, contributorLanguages, totalLanguageContributions);
            }

            // Calculate percentages and save to database
            Map<String, Map<String, Double>> contributorLanguagePercentages = calculatePercentages(
                contributorLanguages, 
                totalLanguageContributions, 
                repo, 
                clr
            );

            Map<String, Object> result = new HashMap<>();
            result.put("percentages", contributorLanguagePercentages);
            result.put("totalContributions", totalLanguageContributions);
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Error analyzing contributor languages: " + e.getMessage(), e);
        }
    }

    private void processContributorCommits(
            String owner, 
            String repo, 
            String username, 
            HttpEntity<Void> entity,
            Map<String, Map<String, Integer>> contributorLanguages,
            Map<String, Integer> totalLanguageContributions) {
        
        String commitsUrl = String.format("https://api.github.com/repos/%s/%s/commits?author=%s&per_page=100", 
            owner, repo, username);
        
        Map<String, Integer> languageContributions = new HashMap<>();
        contributorLanguages.put(username, languageContributions);

        try {
            ResponseEntity<List<Map<String, Object>>> commitsResponse = restTemplate.exchange(
                commitsUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            if (commitsResponse.getBody() == null) return;

            for (Map<String, Object> commit : commitsResponse.getBody()) {
                String sha = (String) commit.get("sha");
                if (sha == null) continue;

                processCommitFiles(owner, repo, sha, entity, languageContributions, totalLanguageContributions);
            }
        } catch (Exception e) {
            System.err.println("Error processing commits for user " + username + ": " + e.getMessage());
        }
    }

    private void processCommitFiles(
            String owner,
            String repo,
            String sha,
            HttpEntity<Void> entity,
            Map<String, Integer> languageContributions,
            Map<String, Integer> totalLanguageContributions) {
        
        String commitUrl = String.format("https://api.github.com/repos/%s/%s/commits/%s", owner, repo, sha);
        
        try {
            ResponseEntity<Map<String, Object>> commitResponse = restTemplate.exchange(
                commitUrl,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> commitData = commitResponse.getBody();
            if (commitData == null) return;

            List<Map<String, Object>> files = (List<Map<String, Object>>) commitData.get("files");
            if (files == null) return;

            for (Map<String, Object> file : files) {
                String filename = (String) file.get("filename");
                if (filename == null || filename.contains(".json") || filename.contains(".lock") || filename.contains(".md") || filename.contains(".png")) continue;

                Integer additions = (Integer) file.get("additions");
                Integer deletions = (Integer) file.get("deletions");
                if (additions == null) additions = 0;
                if (deletions == null) deletions = 0;

                String language = getFileLanguage(filename);
                int changes = additions + deletions;

                languageContributions.merge(language, changes, Integer::sum);
                totalLanguageContributions.merge(language, changes, Integer::sum);
            }
        } catch (Exception e) {
            System.err.println("Error processing commit " + sha + ": " + e.getMessage());
        }
    }

    private Map<String, Map<String, Double>> calculatePercentages(
            Map<String, Map<String, Integer>> contributorLanguages,
            Map<String, Integer> totalLanguageContributions,
            String repo,
            ContributerLanguageRepository clr) {
        
        Map<String, Map<String, Double>> percentages = new HashMap<>();

        for (Map.Entry<String, Map<String, Integer>> entry : contributorLanguages.entrySet()) {
            String contributor = entry.getKey();
            Map<String, Integer> languages = entry.getValue();
            Map<String, Double> contributorPercentages = new HashMap<>();
            percentages.put(contributor, contributorPercentages);

            for (Map.Entry<String, Integer> langEntry : languages.entrySet()) {
                String language = langEntry.getKey();
                int contributorTotal = langEntry.getValue();
                int overallTotal = totalLanguageContributions.get(language);

                if (overallTotal > 0) {
                    double percentage = (double) contributorTotal / overallTotal * 100;
                    contributorPercentages.put(language, percentage);

                    // Save to database
                    ContributorLangauge contributorLanguage = new ContributorLangauge(
                        0, contributor, contributorTotal, repo, language, percentage);
                    clr.save(contributorLanguage);
                }
            }
        }

        return percentages;
    }

    private String getFileLanguage(String filename) {
        if (filename == null) return "Other";
        filename = filename.toLowerCase();
        
        Map<String, String> extensions = Map.ofEntries(
        	    Map.entry(".js", "JavaScript"),
        	    Map.entry(".jsx", "React"),

        	    Map.entry(".java", "Java"),
        	    Map.entry(".html", "HTML"),
        	    Map.entry(".css", "CSS"),
        	    Map.entry(".xml", "XML"),
        	    Map.entry(".py", "Python"),
        	    Map.entry(".cpp", "C++"),
        	    Map.entry(".c", "C"),
        	    Map.entry(".sql", "SQL"),
        	    Map.entry(".go", "Go"),
        	    Map.entry(".rb", "Ruby"),
        	    Map.entry(".php", "PHP"),
        	    Map.entry(".ts", "TypeScript"),
        	    Map.entry(".swift", "Swift"),
        	    Map.entry(".kt", "Kotlin"),
        	    Map.entry(".rs", "Rust")
 
        		);

        for (Map.Entry<String, String> entry : extensions.entrySet()) {
            if (filename.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return "Other";
    }
}