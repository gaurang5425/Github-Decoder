package com.example.github.github_Decoder.Duration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ProjectDuration {
    private String firstCommitDate;
    private String lastCommitDate;
    private long durationInDays;
    private int totalCommits;
    private double commitsPerDay;

    public ProjectDuration(String firstCommitDate, String lastCommitDate, int totalCommits) throws ParseException {
        this.firstCommitDate = firstCommitDate;
        this.lastCommitDate = lastCommitDate;
        this.totalCommits = totalCommits;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date firstCommit = sdf.parse(firstCommitDate);
        Date lastCommit = sdf.parse(lastCommitDate);

        this.durationInDays = (lastCommit.getTime() - firstCommit.getTime()) / (1000 * 60 * 60 * 24);
        this.commitsPerDay = durationInDays > 0 ? (double) totalCommits / durationInDays : totalCommits;
    }

    public String getFirstCommitDate() {
        return firstCommitDate;
    }

    public void setFirstCommitDate(String firstCommitDate) {
        this.firstCommitDate = firstCommitDate;
    }

    public String getLastCommitDate() {
        return lastCommitDate;
    }

    public void setLastCommitDate(String lastCommitDate) {
        this.lastCommitDate = lastCommitDate;
    }

    public long getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(long durationInDays) {
        this.durationInDays = durationInDays;
    }

    public int getTotalCommits() {
        return totalCommits;
    }

    public void setTotalCommits(int totalCommits) {
        this.totalCommits = totalCommits;
    }

    public double getCommitsPerDay() {
        return commitsPerDay;
    }

    public void setCommitsPerDay(double commitsPerDay) {
        this.commitsPerDay = commitsPerDay;
    }

    public ProjectDuration() {
        super();
        // TODO Auto-generated constructor stub
    }



}