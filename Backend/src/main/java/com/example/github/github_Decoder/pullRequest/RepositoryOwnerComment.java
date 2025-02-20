package com.example.github.github_Decoder.pullRequest;

public class RepositoryOwnerComment {
    private String repositoryName;
    private String ownerName;
    private String comment;
    
    public RepositoryOwnerComment() {}
    
    public String getRepositoryName() {
        return repositoryName;
    }
    
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
}