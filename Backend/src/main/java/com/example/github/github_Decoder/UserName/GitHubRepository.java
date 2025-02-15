package com.example.github.github_Decoder.UserName;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class GitHubRepository {
    private String name;
    
    @JsonProperty("html_url")
    private String htmlUrl;
    
    private String language;
    private List<String> languages;
    private int followers;
       private int watchers;
    private int stars;
       public int getWatchers() {
		return watchers;
	}



	public int getStars() {
		return stars;
	}



	public void setStars(int stars) {
		this.stars = stars;
	}



	public void setWatchers(int watchers) {
		this.watchers = watchers;
	}

	private String login;
    public String getLanguage() {
		return language;
	}

    
  
	public void setLanguage(String language) {
		this.language = language;
	}

	@JsonProperty("avatar_url")
    private String avatarUrl;
    
    // Default constructor
    public GitHubRepository() {
        super();
    }
    
    // Parameterized constructor

 
 
 
 
 
 
 
   
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getHtmlUrl() {
        return htmlUrl;
    }
    
    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }
    
    public List<String> getLanguages() {
        return languages;
    }
    
    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
    
    public String getAvatarUrl() {
        return avatarUrl;
    }
    
    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(int followers) {
		this.followers = followers;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	GitHubRepository(String name, String htmlUrl, String language, List<String> languages, int followers, String login,
			String avatarUrl,int watchers,int stars) {
		super();
		this.name = name;
		this.htmlUrl = htmlUrl;
		this.language = language;
		this.languages = languages;
		this.followers = followers;
		this.login = login;
		this.avatarUrl = avatarUrl;
		this.stars=stars;

		this.watchers=watchers;
	}
}