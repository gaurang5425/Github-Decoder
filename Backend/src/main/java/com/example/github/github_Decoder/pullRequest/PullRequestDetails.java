package com.example.github.github_Decoder.pullRequest;
public class PullRequestDetails {
    private long prId;
    private String repositoryName;
    private String title;
    private String status;  // ACCEPTED, REJECTED, PENDING
    
    public PullRequestDetails() {}
    
    public long getPrId() {
        return prId;
    }
    
    public void setPrId(long prId) {
        this.prId = prId;
    }
    
    public String getRepositoryName() {
        return repositoryName;
    }
    
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}