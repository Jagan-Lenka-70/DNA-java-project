package com.bioinformatics.dna.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "analysis_records")
public class AnalysisRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlgorithmType algorithmType;

    @Column(nullable = false, length = 2000)
    private String sequence1;

    @Column(nullable = false, length = 2000)
    private String sequence2;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private double identityPercentage;

    @Column(nullable = false, length = 3000)
    private String alignedSequence1;

    @Column(nullable = false, length = 3000)
    private String alignedSequence2;

    @Column(nullable = false, length = 3000)
    private String alignmentMarker;

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public UserAccount getUser() {
        return user;
    }

    public void setUser(UserAccount user) {
        this.user = user;
    }

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getSequence1() {
        return sequence1;
    }

    public void setSequence1(String sequence1) {
        this.sequence1 = sequence1;
    }

    public String getSequence2() {
        return sequence2;
    }

    public void setSequence2(String sequence2) {
        this.sequence2 = sequence2;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getIdentityPercentage() {
        return identityPercentage;
    }

    public void setIdentityPercentage(double identityPercentage) {
        this.identityPercentage = identityPercentage;
    }

    public String getAlignedSequence1() {
        return alignedSequence1;
    }

    public void setAlignedSequence1(String alignedSequence1) {
        this.alignedSequence1 = alignedSequence1;
    }

    public String getAlignedSequence2() {
        return alignedSequence2;
    }

    public void setAlignedSequence2(String alignedSequence2) {
        this.alignedSequence2 = alignedSequence2;
    }

    public String getAlignmentMarker() {
        return alignmentMarker;
    }

    public void setAlignmentMarker(String alignmentMarker) {
        this.alignmentMarker = alignmentMarker;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
