	package com.example.github.github_Decoder.UserName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubRepositoryController {
    
    @Autowired
    private GitHubRepositoryService gitHubRepositoryService;
    
    @GetMapping("/repositories/{username}")
    public List<GitHubRepository> getUserRepositories(@PathVariable String username) {
        return gitHubRepositoryService.getUserRepositories(username);
    }
}