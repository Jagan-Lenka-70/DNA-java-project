package com.bioinformatics.dna.dto;

import com.bioinformatics.dna.model.AlgorithmType;

import java.time.Instant;

public class HistoryItemDto {
    private Long id;
    private AlgorithmType algorithmType;
    private String sequence1;
    private String sequence2;
    private int score;
    private double identityPercentage;
    private String alignedSequence1;
    private String alignedSequence2;
    private String alignmentMarker;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
