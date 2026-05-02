package com.bioinformatics.dna.service;

import com.bioinformatics.dna.dto.AuthDtos;
import com.bioinformatics.dna.exception.ApiException;
import com.bioinformatics.dna.model.AuthToken;
import com.bioinformatics.dna.model.UserAccount;
import com.bioinformatics.dna.repository.AuthTokenRepository;
import com.bioinformatics.dna.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final AuthTokenRepository authTokenRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserAccountRepository userAccountRepository, AuthTokenRepository authTokenRepository) {
        this.userAccountRepository = userAccountRepository;
        this.authTokenRepository = authTokenRepository;
    }

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        if (userAccountRepository.existsByUsername(request.getUsername())) {
            throw new ApiException(HttpStatus.CONFLICT, "Username already exists");
        }
        if (userAccountRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already exists");
        }
        UserAccount user = new UserAccount();
        user.setUsername(request.getUsername().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(encoder.encode(request.getPassword()));
        UserAccount saved = userAccountRepository.save(user);
        return createSession(saved);
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String raw = request.getUsernameOrEmail().trim();
        Optional<UserAccount> byUsername = userAccountRepository.findByUsername(raw);
        Optional<UserAccount> byEmail = userAccountRepository.findByEmail(raw.toLowerCase());
        UserAccount user = byUsername.or(() -> byEmail)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return createSession(user);
    }

    @Transactional
    public void logout(String tokenValue) {
        AuthToken token = authTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid token"));
        token.setRevoked(true);
        authTokenRepository.save(token);
    }

    public UserAccount requireUser(String tokenValue) {
        AuthToken token = authTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Missing or invalid token"));
        if (token.isRevoked() || token.getExpiresAt().isBefore(Instant.now())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Session expired. Please login again.");
        }
        return token.getUser();
    }

    private AuthDtos.AuthResponse createSession(UserAccount user) {
        String tokenValue = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        AuthToken token = new AuthToken();
        token.setUser(user);
        token.setToken(tokenValue);
        token.setRevoked(false);
        token.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
        authTokenRepository.save(token);
        return new AuthDtos.AuthResponse(tokenValue, user.getUsername(), user.getEmail());
    }
}
