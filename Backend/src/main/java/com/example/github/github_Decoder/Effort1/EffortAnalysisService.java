package com.example.github.github_Decoder.Effort1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EffortAnalysisService {
    
    @Autowired
    private EffortCalculator effortCalculator;
    
    @Autowired
    private GitHubService gitHubService;
    
    public EffortResponse analyzeEffortByRepo(String owner, String repo) {
        // Get contributors
        List<Map<String, Object>> contributors = gitHubService.getContributors(owner, repo);
        
        // Get language statistics
        Map<String, Long> languageStats = gitHubService.getLanguages(owner, repo);
        
        // Calculate language efforts
        Map<String, LanguageEffortDetails> languageEfforts = calculateLanguageEfforts(languageStats);
        
        // Map to store contributor effort hours
        Map<String, Double> contributorEffortMap = new HashMap<>();
        
        // Initialize contributor effort map with zero effort
        for (Map<String, Object> contributor : contributors) {
            String login = (String) contributor.get("login");
            contributorEffortMap.put(login, 0.0);
        }
        
        // Get files and their contributors from the repository
        List<Map<String, Object>> files = gitHubService.getRepositoryFiles(owner, repo);
        
        // Process each file and assign effort to contributors
        for (Map<String, Object> file : files) {
            String language = (String) file.get("language");
            System.out.print(language);
           if (language == null || language.contains(".json") || language.contains(".lock") || language.contains(".md") || language.contains(".png")) continue;
            double fileEffortHours = effortCalculator.calculateEffortHours(language, 1);
            
            // Get contributors for this file
            List<String> fileContributors = (List<String>) file.get("contributors");
            
            if (fileContributors != null && !fileContributors.isEmpty()) {
                // Distribute file effort among contributors
                double effortPerContributor = fileEffortHours / fileContributors.size();
                
                for (String contributor : fileContributors) {
                    // Add this file's effort to the contributor's total
                    double currentEffort = contributorEffortMap.getOrDefault(contributor, 0.0);
                    contributorEffortMap.put(contributor, currentEffort + effortPerContributor);
                }
            }
        }
        
        // Calculate total effort hours
        double totalEffortHours = contributorEffortMap.values().stream().mapToDouble(Double::doubleValue).sum();
        
        // Create contributor effort details
        Map<String, ContributorEffortDetails> contributorEfforts = new HashMap<>();
        
        for (Map<String, Object> contributor : contributors) {
            String login = (String) contributor.get("login");
            int contributions = ((Number) contributor.get("contributions")).intValue();
            
            double contributorEffortHours = contributorEffortMap.getOrDefault(login, 0.0);
            double effortPercentage = (totalEffortHours > 0) ? 
                    (contributorEffortHours / totalEffortHours) * 100 : 0.0;
            
            ContributorEffortDetails details = new ContributorEffortDetails(login, contributions);
            details.setEffortHours(contributorEffortHours);
            details.setEffortPercentage(effortPercentage);
            
            contributorEfforts.put(login, details);
        }
        
        return new EffortResponse(
            owner,
            repo,
            languageEfforts,
            contributorEfforts,
            calculateTotalFiles(languageEfforts),
            totalEffortHours
        );
    }
    
    private Map<String, LanguageEffortDetails> calculateLanguageEfforts(Map<String, Long> languageStats) {
        Map<String, LanguageEffortDetails> efforts = new HashMap<>();
        long totalBytes = languageStats.values().stream().mapToLong(Long::longValue).sum();
        
        for (Map.Entry<String, Long> entry : languageStats.entrySet()) {
            String language = entry.getKey();
            Long bytes = entry.getValue();
            double percentage = (bytes.doubleValue() / totalBytes) * 100;
            
            // Estimate file count based on bytes (this is a rough approximation)
            int estimatedFiles = (int) (bytes / 2048); // Assuming average file size of 2KB
            double effortHours = effortCalculator.calculateEffortHours(language, estimatedFiles);
            
            LanguageEffortDetails details = new LanguageEffortDetails(
                estimatedFiles,
                percentage,
                effortHours,
                percentage  // Using same percentage for effort as a simple metric
            );
            
            efforts.put(language, details);
        }
        
        return efforts;
    }
    
    private int calculateTotalFiles(Map<String, LanguageEffortDetails> languageEfforts) {
        return languageEfforts.values().stream()
            .mapToInt(LanguageEffortDetails::getFileCount)
            .sum();
    }
}