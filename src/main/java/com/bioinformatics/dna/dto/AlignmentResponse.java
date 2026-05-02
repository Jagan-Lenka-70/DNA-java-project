package com.bioinformatics.dna.dto;

import com.bioinformatics.dna.model.AlgorithmType;

import java.util.List;

public class AlignmentResponse {

    private AlgorithmType algorithmType;
    private String normalizedSequence1;
    private String normalizedSequence2;
    private String alignedSequence1;
    private String alignmentMarker;
    private String alignedSequence2;
    private int score;
    private double identityPercentage;
    private List<List<Integer>> scoringMatrix;
    private List<CellPoint> tracebackPath;
    private String explanation;

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getNormalizedSequence1() {
        return normalizedSequence1;
    }

    public void setNormalizedSequence1(String normalizedSequence1) {
        this.normalizedSequence1 = normalizedSequence1;
    }

    public String getNormalizedSequence2() {
        return normalizedSequence2;
    }

    public void setNormalizedSequence2(String normalizedSequence2) {
        this.normalizedSequence2 = normalizedSequence2;
    }

    public String getAlignedSequence1() {
        return alignedSequence1;
    }

    public void setAlignedSequence1(String alignedSequence1) {
        this.alignedSequence1 = alignedSequence1;
    }

    public String getAlignmentMarker() {
        return alignmentMarker;
    }

    public void setAlignmentMarker(String alignmentMarker) {
        this.alignmentMarker = alignmentMarker;
    }

    public String getAlignedSequence2() {
        return alignedSequence2;
    }

    public void setAlignedSequence2(String alignedSequence2) {
        this.alignedSequence2 = alignedSequence2;
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

    public List<List<Integer>> getScoringMatrix() {
        return scoringMatrix;
    }

    public void setScoringMatrix(List<List<Integer>> scoringMatrix) {
        this.scoringMatrix = scoringMatrix;
    }

    public List<CellPoint> getTracebackPath() {
        return tracebackPath;
    }

    public void setTracebackPath(List<CellPoint> tracebackPath) {
        this.tracebackPath = tracebackPath;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public static class CellPoint {
        private int row;
        private int col;

        public CellPoint() {
        }

        public CellPoint(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int getRow() {
            return row;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public int getCol() {
            return col;
        }

        public void setCol(int col) {
            this.col = col;
        }
    }
}
