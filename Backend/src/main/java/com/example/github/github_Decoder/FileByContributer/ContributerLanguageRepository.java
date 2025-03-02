package com.example.github.github_Decoder.FileByContributer;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.github.github_Decoder.Contributer.Contributer;

public interface ContributerLanguageRepository extends JpaRepository<ContributorLangauge,Integer> {
List<ContributorLangauge> findByRepoName(String repoName);
    
    // Check if entries exist for a repository
    boolean existsByRepoName(String repoName);
    
    // Optional: Custom query to get formatted data
    @Query("SELECT new map(cl.login as contributor, cl.language as language, cl.percentage as percentage) " +
           "FROM ContributorLangauge cl WHERE cl.repoName = :repoName")
    List<Map<String, Object>> findFormattedDataByRepoName(@Param("repoName") String repoName);
}
 