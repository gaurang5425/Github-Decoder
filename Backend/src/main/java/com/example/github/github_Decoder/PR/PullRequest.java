package com.example.github.github_Decoder.PR;

public class PullRequest {
    private String title;
    private String state;
    private String html_url;
    private String url;
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getHtmlUrl() { return html_url; }
    
    public void setHtmlUrl(String html_url) { this.html_url = html_url; }
    
    public String getUrl() { return url; }
    
    public void setUrl(String url) { this.url = url; }
}
