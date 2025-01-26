package com.example.github.github_Decoder.Contributer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



import java.util.List;

@RestController
public class ContributerController {
	@Autowired
	ContributerRepository cr;
    private final ContributerService contributerService;

    public ContributerController(ContributerService contributerService) {
        this.contributerService = contributerService;
    }

    @GetMapping("/contributors")
    public List<Contributer> getContributors(
            @RequestParam String owner,
            @RequestParam String repo) {
    	List<Contributer> l=cr.findByRepoName(repo);
    	if(l.isEmpty()) {

            List<Contributer> cs= contributerService.getContributors(owner, repo);
            for(Contributer c:cs)
            {   
            	c.setRepoName(repo);
            	cr.save(c);
            }
            return cs;
    	}
    
    		return l;
    	
    }
}
