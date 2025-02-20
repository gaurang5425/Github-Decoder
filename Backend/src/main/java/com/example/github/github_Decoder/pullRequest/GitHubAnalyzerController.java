package com.example.github.github_Decoder.pullRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
public class GitHubAnalyzerController {
    
    private final GitHubAnalyzerService analyzerService;
    
    public GitHubAnalyzerController(GitHubAnalyzerService analyzerService) {
        this.analyzerService = analyzerService;
    }
    
    @GetMapping("/analyze/{username}/pr")
    public ResponseEntity<PullRequestAnalysis> analyzePullRequests(@PathVariable String username) throws Exception {
        PullRequestAnalysis analysis = analyzerService.analyzePullRequests(username);
        return ResponseEntity.ok(analysis);
    }
}