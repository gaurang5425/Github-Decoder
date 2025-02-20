package com.example.github.github_Decoder.pullRequest;

import java.util.ArrayList;
import java.util.List;

public class PullRequestAnalysis {
    private String username;
    private int totalPRs;
    private int acceptedPRs;
    private int rejectedPRs;
    private List<PullRequestDetails> pullRequests;
    private List<RepositoryOwnerComment> ownerComments;
   
   
 
    
    public PullRequestAnalysis() {
        this.pullRequests = new ArrayList<>();
        this.ownerComments = new ArrayList<>();
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public List<PullRequestDetails> getPullRequests() {
        return pullRequests;
    }
    
    public void setPullRequests(List<PullRequestDetails> pullRequests) {
        this.pullRequests = pullRequests;
    }
    
    public int getTotalPRs() {
        return totalPRs;
    }
    
    public void setTotalPRs(int totalPRs) {
        this.totalPRs = totalPRs;
    }
    
    public int getAcceptedPRs() {
        return acceptedPRs;
    }
    
    public void setAcceptedPRs(int acceptedPRs) {
        this.acceptedPRs = acceptedPRs;
    }
    
    public int getRejectedPRs() {
        return rejectedPRs;
    }
    
    public void setRejectedPRs(int rejectedPRs) {
        this.rejectedPRs = rejectedPRs;
    }
    
    public List<RepositoryOwnerComment> getOwnerComments() {
        return ownerComments;
    }
    
    public void setOwnerComments(List<RepositoryOwnerComment> ownerComments) {
        this.ownerComments = ownerComments;
    }
    
    
}