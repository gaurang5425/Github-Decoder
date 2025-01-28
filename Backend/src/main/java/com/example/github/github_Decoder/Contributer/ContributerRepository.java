package com.example.github.github_Decoder.Contributer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;





public interface ContributerRepository extends JpaRepository<Contributer,Integer> {
	public List<Contributer> findByRepoName(String reponame);
}
