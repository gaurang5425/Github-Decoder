package com.example.github.github_Decoder.Effort1;

import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "effort_analysis")
public class EffortResponse {
	
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	  private String owner;
	    private String repoName;
	 
	    public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		 @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
		    @JoinColumn(name = "effort_analysis_id")
	    private Map<String, LanguageEffortDetails> languageEfforts;
		    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
		    @JoinColumn(name = "effort_analysis_id")
	    private Map<String, ContributorEffortDetails> contributorEfforts;
	    private int totalFiles;
	    private double totalEffortHours;
	    
	    // Update constructor and add getter/setters for contributorEfforts
	    public EffortResponse(String owner, String repoName, 
	                         Map<String, LanguageEffortDetails> languageEfforts,
	                         Map<String, ContributorEffortDetails> contributorEfforts,
	                         int totalFiles, double totalEffortHours) {
	        this.owner = owner;
	        this.repoName = repoName;
	        this.languageEfforts = languageEfforts;
	        this.contributorEfforts = contributorEfforts;
	        this.totalFiles = totalFiles;
	        this.totalEffortHours = totalEffortHours;
	    }

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public String getRepoName() {
			return repoName;
		}

		public void setRepoName(String repoName) {
			this.repoName = repoName;
		}

		public Map<String, LanguageEffortDetails> getLanguageEfforts() {
			return languageEfforts;
		}

		public void setLanguageEfforts(Map<String, LanguageEffortDetails> languageEfforts) {
			this.languageEfforts = languageEfforts;
		}

		public Map<String, ContributorEffortDetails> getContributorEfforts() {
			return contributorEfforts;
		}

		public void setContributorEfforts(Map<String, ContributorEffortDetails> contributorEfforts) {
			this.contributorEfforts = contributorEfforts;
		}

		public int getTotalFiles() {
			return totalFiles;
		}

		public void setTotalFiles(int totalFiles) {
			this.totalFiles = totalFiles;
		}

		public double getTotalEffortHours() {
			return totalEffortHours;
		}

		public void setTotalEffortHours(double totalEffortHours) {
			this.totalEffortHours = totalEffortHours;
		}

		public EffortResponse() {
			super();
			// TODO Auto-generated constructor stub
		}

		public EffortResponse(EffortResponse response) {
		    this.owner = response.getOwner();
		    this.repoName = response.getRepoName();
		    this.languageEfforts = response.getLanguageEfforts();
		    this.contributorEfforts = response.getContributorEfforts();
		    this.totalFiles = response.getTotalFiles();
		    this.totalEffortHours = response.getTotalEffortHours();
		}
		public EffortResponse toEffortResponse() {
		    return new EffortResponse(
		        this.owner,
		        this.repoName,
		        this.languageEfforts,
		        this.contributorEfforts,
		        this.totalFiles,
		        this.totalEffortHours
		    );
		}

	
}
