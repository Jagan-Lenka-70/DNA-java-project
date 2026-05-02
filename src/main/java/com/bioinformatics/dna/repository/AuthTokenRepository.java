package com.bioinformatics.dna.repository;

import com.bioinformatics.dna.model.AuthToken;
import com.bioinformatics.dna.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByToken(String token);
    void deleteByUser(UserAccount user);
}
