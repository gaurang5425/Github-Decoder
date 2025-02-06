package com.example.github.github_Decoder.PR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
public class DetailedPullRequestController {
    
    @Autowired
    private DetailedGitHubPullRequestService service;

    @GetMapping("/user/{username}/pr")
    public DetailedPullRequestStats getPullRequestStats(
        @PathVariable String username
    ) {
        return service.getComprehensivePullRequestStats(username);
    }
}