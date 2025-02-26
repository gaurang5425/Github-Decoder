package com.example.github.github_Decoder.Effort1;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "contributor_efforts")
public class ContributorEffortDetails {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	  private String login;
	    private int commits;
	    private double effortHours;
	    private double effortPercentage;


	    ContributorEffortDetails() {
			super();
			// TODO Auto-generated constructor stub
		}


		public ContributorEffortDetails(String login, int commits) {
	        this.login = login;
	        this.commits = commits;
	    
	    }
	    

		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public String getLogin() {
			return login;
		}

		public void setLogin(String login) {
			this.login = login;
		}

		public int getCommits() {
			return commits;
		}

		public void setCommits(int commits) {
			this.commits = commits;
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
	    
}
