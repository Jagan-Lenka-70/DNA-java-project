package com.bioinformatics.dna.dto;

import com.bioinformatics.dna.model.AlgorithmType;

import java.util.ArrayList;
import java.util.List;

public class BatchReportDto {
    private AlgorithmType algorithmType;
    private String referenceHeader;
    private String referenceSequence;
    private List<BatchItem> results = new ArrayList<>();

    public AlgorithmType getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(AlgorithmType algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getReferenceHeader() {
        return referenceHeader;
    }

    public void setReferenceHeader(String referenceHeader) {
        this.referenceHeader = referenceHeader;
    }

    public String getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(String referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public List<BatchItem> getResults() {
        return results;
    }

    public void setResults(List<BatchItem> results) {
        this.results = results;
    }

    public static class BatchItem {
        private String header;
        private String sequence;
        private int score;
        private double identityPercentage;
        private String alignedSequence1;
        private String alignmentMarker;
        private String alignedSequence2;

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getSequence() {
            return sequence;
        }

        public void setSequence(String sequence) {
            this.sequence = sequence;
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
    }
}
