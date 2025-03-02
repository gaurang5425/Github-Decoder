package com.example.github.github_Decoder.FileByContributer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ContributorLangauge {
	@Id @GeneratedValue(strategy= GenerationType.IDENTITY)
	private int Contributor_Langauge_id;
    private String login; 
    private int total;    
     private String repoName;
     private String language;
     private double percentage;
    public ContributorLangauge(int contributor_Langauge_id, String login, int total, String repoName, String language,
			double percentage) {
		super();
		Contributor_Langauge_id = contributor_Langauge_id;
		this.login = login;
		this.total = total;
		this.repoName = repoName;
		this.language = language;
		this.percentage = percentage;
	}

	
	public ContributorLangauge() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public double getPercentage() {
		return percentage;
	}


	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}


	public String getLogin() {
		return login;
	}


	public void setLogin(String login) {
		this.login = login;
	}


	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	// Getters and Setters
    

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // Inner class for the "author" field
    
}

