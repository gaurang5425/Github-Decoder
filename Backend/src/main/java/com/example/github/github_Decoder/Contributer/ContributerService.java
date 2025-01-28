package com.example.github.github_Decoder.Contributer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;





@Service
public class ContributerService {
	 @Value("${github.token}")
	    private String githubToken;

	    public List<Contributer> getContributors(String owner, String repo) {

	    	
	        String url = "https://api.github.com/repos/" + owner + "/" + repo + "/contributors";

	        RestTemplate restTemplate = new RestTemplate();
	        var headers = new org.springframework.http.HttpHeaders();
	        headers.set("Authorization", "Bearer " + githubToken);
	        var entity = new org.springframework.http.HttpEntity<>(headers);

	        var response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Contributer[].class);

	        return Arrays.asList(response.getBody());
	    }
}
