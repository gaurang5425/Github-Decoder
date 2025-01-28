package com.example.github.github_Decoder.Contributer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Contributer {
	@Id @GeneratedValue(strategy= GenerationType.IDENTITY)
	private int Contributer_id;
	
	private String login;
    private int contributions;
    private String repoName;
    
    public String getLogin() {
        return login;
    }

    public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	public void setLogin(String login) {
        this.login = login;
    }

    public int getContributions() {
        return contributions;
    }

    public void setContributions(int contributions) {
        this.contributions = contributions;
    }
}
