package com.example.github.github_Decoder.Effort1;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EffortAnalysisRepository extends JpaRepository<EffortResponse, Long> {
      Optional<EffortResponse> findByOwnerAndRepoName(String owner, String repoName) ;


}