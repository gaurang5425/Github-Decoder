package com.example.github.github_Decoder.FileByContributer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/languages")
public class ContributorLanguageController {
    private final ContributorLanguageService contributorLanguageService;
    private final ContributerLanguageRepository clr;

    @Autowired
    public ContributorLanguageController(ContributorLanguageService contributorLanguageService,
            ContributerLanguageRepository clr) {
        this.contributorLanguageService = contributorLanguageService;
        this.clr = clr;
    }

    @GetMapping("/{owner}/{repo}")
    public ResponseEntity<Map<String, Object>> analyzeLanguages(
            @PathVariable String owner,
            @PathVariable String repo) {
        if (clr.existsByRepoName(repo)) {
            System.out.println("Fetching data from database for repo: " + repo);
            List<ContributorLangauge> existingData = clr.findByRepoName(repo);
            Map<String, Object> response = formatDatabaseData(existingData);
            return ResponseEntity.ok(response);
        }
        
        System.out.println("Calling service for repo: " + repo);
        Map<String, Object> analysis = contributorLanguageService.analyzeContributorLanguages(owner, repo, clr);
        return ResponseEntity.ok(analysis);
    }

    private Map<String, Object> formatDatabaseData(List<ContributorLangauge> data) {
        Map<String, Object> response = new HashMap<>();
        Map<String, Map<String, Double>> percentages = new HashMap<>();
        Map<String, Integer> totalContributions = new HashMap<>();

        try {
            // Process each contribution
            for (ContributorLangauge contribution : data) {
                String login = contribution.getLogin();
                String language = contribution.getLanguage();
                double percentage = contribution.getPercentage();
                int total = contribution.getTotal();

                // Handle percentages map
                if (!percentages.containsKey(login)) {
                    percentages.put(login, new HashMap<>());
                }
                percentages.get(login).put(language, percentage);

                // Handle total contributions
                totalContributions.put(language, 
                    totalContributions.getOrDefault(language, 0) + total);
            }

            response.put("percentages", percentages);
            response.put("totalContributions", totalContributions);

        } catch (Exception e) {
            System.err.println("Error processing contributor data: " + e.getMessage());
            response.put("error", "Failed to process contributor data");
        }

        return response;
    }
}