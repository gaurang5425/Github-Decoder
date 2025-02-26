package com.example.github.github_Decoder.lifetime_commit;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class GithubUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String username;
    
    private LocalDateTime lastUpdated;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Repository> repositories = new ArrayList<>();
    
    // Constructors
    public GithubUser() {}
    
    public GithubUser(String username) {
        this.username = username;
        this.lastUpdated = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public List<Repository> getRepositories() {
        return repositories;
    }
    
    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }
    
    public void addRepository(Repository repository) {
        this.repositories.add(repository);
        repository.setUser(this);
    }
}
