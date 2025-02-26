package com.example.github.github_Decoder.lifetime_commit;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class CommitStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "commit_month") 
    private String yearMonth; // Format: YYYY-MM
    private int commitCount;
    
    @ManyToOne
    @JoinColumn(name = "repository_id")
    @JsonBackReference
    private Repository repository;
    
    // Constructors
    public CommitStat() {}
    
    public CommitStat(String yearMonth, int commitCount) {
        this.yearMonth = yearMonth;
        this.commitCount = commitCount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getYearMonth() {
        return yearMonth;
    }
    
    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
    
    public int getCommitCount() {
        return commitCount;
    }
    
    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }
    
    public Repository getRepository() {
        return repository;
    }
    
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}