package com.example.github.github_Decoder.Effort1;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/effort")
public class EffortController {

    private final EffortAnalysisService effortAnalysisService;
    private final EffortAnalysisRepository effortAnalysisRepository;

    @Autowired
    public EffortController(EffortAnalysisService effortAnalysisService, 
                          EffortAnalysisRepository effortAnalysisRepository) {
        this.effortAnalysisService = effortAnalysisService;
        this.effortAnalysisRepository = effortAnalysisRepository;
    }

    @GetMapping("/{owner}/{repo}/analyze")
    public ResponseEntity<Map<String, Object>> analyzeRepository(
            @PathVariable String owner,
            @PathVariable String repo) {

        try {
            // Check if analysis exists in database
            Optional<EffortResponse> existingAnalysis = effortAnalysisRepository.findByOwnerAndRepoName(owner, repo);

            EffortResponse response;

            if (existingAnalysis.isPresent()) {
                System.out.println("Fetching existing analysis from database for " + owner + "/" + repo);
                response = existingAnalysis.get();
            } else {
                System.out.println("Performing new analysis for " + owner + "/" + repo);
                // Perform new analysis
                response = effortAnalysisService.analyzeEffortByRepo(owner, repo);

                if (response == null) {
                    System.err.println("Analysis failed for " + owner + "/" + repo);
                    return ResponseEntity.notFound().build();
                }

                // Store new analysis in database
                try {
                    effortAnalysisRepository.save(response);
                    System.out.println("Successfully stored analysis for " + owner + "/" + repo);
                } catch (Exception e) {
                    System.err.println("Failed to store analysis in database: " + e.getMessage());
                    // Continue anyway since we have the analysis results
                }
            }

            // Format response
            Map<String, Object> formattedResponse = formatResponse(response);
            return ResponseEntity.ok(formattedResponse);

        } catch (Exception e) {
            System.err.println("Error processing request: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to process request: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    private Map<String, Object> formatResponse(EffortResponse response) {
        Map<String, Object> formattedResponse = new HashMap<>();
        formattedResponse.put("totalEffortHours", response.getTotalEffortHours());
        formattedResponse.put("languageEfforts", response.getLanguageEfforts());
        formattedResponse.put("contributorEfforts", response.getContributorEfforts());
        return formattedResponse;
    }
}