package com.example.github.github_Decoder.lifetime_commit;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface GithubUserRepository extends JpaRepository<GithubUser, Long> {
    Optional<GithubUser> findByUsername(String username);
}