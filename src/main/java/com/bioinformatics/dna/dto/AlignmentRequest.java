package com.bioinformatics.dna.dto;

import com.bioinformatics.dna.model.AlgorithmType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AlignmentRequest {

    @NotBlank(message = "Sequence 1 is required")
    @Pattern(regexp = "^[ACGTacgt]+$", message = "Sequence 1 must only contain A, C, G, T")
    private String sequence1;

    @NotBlank(message = "Sequence 2 is required")
    @Pattern(regexp = "^[ACGTacgt]+$", message = "Sequence 2 must only contain A, C, G, T")
    private String sequence2;

    @NotNull(message = "Algorithm type is required")
    private AlgorithmType algorithmType;

    @Min(value = -10, message = "Match score must be between -10 and 10")
    @Max(value = 10, message = "Match score must be between -10 and 10")
    private int matchScore = 2;

    @Min(value = -10, message = "Mismatch score must be between -10 and 10")
    @Max(value = 10, message = "Mismatch score must be between -10 and 10")
    private int mismatchScore = -1;

    @Min(value = -10, message = "Gap penalty must be between -10 and 0")
    @Max(value = 0, message = "Gap penalty must be between -10 and 0")
    private int gapPenalty = -2;

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

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }

    public int getMismatchScore() {
        return mismatchScore;
    }

    public void setMismatchScore(int mismatchScore) {
        this.mismatchScore = mismatchScore;
    }

    public int getGapPenalty() {
        return gapPenalty;
    }

    public void setGapPenalty(int gapPenalty) {
        this.gapPenalty = gapPenalty;
    }
}
