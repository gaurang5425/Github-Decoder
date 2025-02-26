package com.example.github.github_Decoder.lifetime_commit;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Repository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private GithubUser user;
    
    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<CommitStat> commitStats = new ArrayList<>();
    
    // Constructors
    public Repository() {}
    
    public Repository(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public GithubUser getUser() {
        return user;
    }
    
    public void setUser(GithubUser user) {
        this.user = user;
    }
    
    public List<CommitStat> getCommitStats() {
        return commitStats;
    }
    
    public void setCommitStats(List<CommitStat> commitStats) {
        this.commitStats = commitStats;
    }
    
    public void addCommitStat(CommitStat commitStat) {
        this.commitStats.add(commitStat);
        commitStat.setRepository(this);
    }
}

