package com.example.github.github_Decoder.Duration;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;



@RestController
@RequestMapping("/api")
public class ProjectDurationController {
    @Autowired
    private final GitHubService1 gitHubService1;

    public ProjectDurationController(GitHubService1 gitHubService1) {
        this.gitHubService1 = gitHubService1;
    }

    @GetMapping("/duration/{owner}/{repo}")
    public ResponseEntity<ProjectDuration> getProjectDuration(
            @PathVariable String owner,
            @PathVariable String repo
    ) throws JsonMappingException, JsonProcessingException, ParseException {

        ProjectDuration duration = gitHubService1.getProjectDuration(owner, repo);
        return ResponseEntity.ok(duration);

    }
}