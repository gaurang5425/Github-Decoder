package com.example.github.github_Decoder.lifetime_commit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/github")
public class GithubController {

    @Autowired
    private GithubService githubService;
    
    @GetMapping("/user/{username}")
    public ResponseEntity<GithubUser> getUserStats(@PathVariable String username) {
        GithubUser userStats = githubService.getUserRepositoriesWithCommits(username);
        return ResponseEntity.ok(userStats);
    }
}

