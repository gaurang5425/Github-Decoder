package com.example.github.github_Decoder.PR;

import java.util.List;

public class RepositoryPullRequestDetail {
    private String repositoryName;
   

	private String html_url;
    private int totalPullRequests;
    private List<PullRequestInfo> pullRequests;

    public static class PullRequestInfo {
        private String title;
        private String state; // open/closed
        private boolean accepted;
        private String maintainerComment;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public boolean isAccepted() {
			return accepted;
		}
		public void setAccepted(boolean accepted) {
			this.accepted = accepted;
		}
		public String getMaintainerComment() {
			return maintainerComment;
		}
		public void setMaintainerComment(String maintainerComment) {
			this.maintainerComment = maintainerComment;
		}
		

          
    }

    public String getHtml_url() {
		return html_url;
	}

	public void setHtml_url(String html_url) {
		this.html_url = html_url;
	}
	RepositoryPullRequestDetail(String repositoryName,String html_url, int totalPullRequests, List<PullRequestInfo> pullRequests) {
		super();
		this.repositoryName = repositoryName;
		this.html_url=html_url;
		this.totalPullRequests = totalPullRequests;
		this.pullRequests = pullRequests;
	}

	public String getRepositoryName() {
		return repositoryName;
	}

	public void setRepositoryName(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	public int getTotalPullRequests() {
		return totalPullRequests;
	}

	public void setTotalPullRequests(int totalPullRequests) {
		this.totalPullRequests = totalPullRequests;
	}

	public List<PullRequestInfo> getPullRequests() {
		return pullRequests;
	}

	public void setPullRequests(List<PullRequestInfo> pullRequests) {
		this.pullRequests = pullRequests;
	}

    // Constructor, Getters, Setters
}