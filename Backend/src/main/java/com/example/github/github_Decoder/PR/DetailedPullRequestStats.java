package com.example.github.github_Decoder.PR;

import java.util.List;

public class DetailedPullRequestStats {
    private String username;
    private int totalRepositories;
    private int totalPullRequests;
    private int totalAcceptedRequest;
    public int getTotalAcceptedRequest() {
		return totalAcceptedRequest;
	}

	public void setTotalAcceptedRequest(int totalAcceptedRequest) {
		this.totalAcceptedRequest = totalAcceptedRequest;
	}

	private List<RepositoryPullRequestDetail> repositoryDetails;

    // Constructor, Getters, Setters
    public DetailedPullRequestStats(String username, int totalRepositories, 
                                    int totalPullRequests,int totalAcceptedRequest,
                                    List<RepositoryPullRequestDetail> repositoryDetails) {
        this.username = username;
        this.totalRepositories = totalRepositories;
        this.totalPullRequests = totalPullRequests;
        this.repositoryDetails = repositoryDetails;
        this.totalAcceptedRequest=totalAcceptedRequest;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getTotalRepositories() {
		return totalRepositories;
	}

	public void setTotalRepositories(int totalRepositories) {
		this.totalRepositories = totalRepositories;
	}

	public int getTotalPullRequests() {
		return totalPullRequests;
	}

	public void setTotalPullRequests(int totalPullRequests) {
		this.totalPullRequests = totalPullRequests;
	}

	public List<RepositoryPullRequestDetail> getRepositoryDetails() {
		return repositoryDetails;
	}

	public void setRepositoryDetails(List<RepositoryPullRequestDetail> repositoryDetails) {
		this.repositoryDetails = repositoryDetails;
	}

    
    // Getters and Setters
}