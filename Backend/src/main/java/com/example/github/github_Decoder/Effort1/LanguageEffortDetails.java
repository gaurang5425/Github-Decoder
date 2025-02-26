package com.example.github.github_Decoder.Effort1;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "language_efforts")

public class LanguageEffortDetails {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	private int fileCount;
    private double percentage;
    private double effortHours;
    private double effortPercentage;
	public int getFileCount() {
		return fileCount;
	}
	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}
	public double getPercentage() {
		return percentage;
	}
	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}
	public double getEffortHours() {
		return effortHours;
	}
	public void setEffortHours(double effortHours) {
		this.effortHours = effortHours;
	}
	public double getEffortPercentage() {
		return effortPercentage;
	}
	public void setEffortPercentage(double effortPercentage) {
		this.effortPercentage = effortPercentage;
	}
	public LanguageEffortDetails(int fileCount, double percentage, double effortHours, double effortPercentage) {
		super();
		this.fileCount = fileCount;
		this.percentage = percentage;
		this.effortHours = effortHours;
		this.effortPercentage = effortPercentage;
	}
	public LanguageEffortDetails() {
		super();
		// TODO Auto-generated constructor stub
	}
    
}
