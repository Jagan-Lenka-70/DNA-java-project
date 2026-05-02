package com.bioinformatics.dna.service;

import com.bioinformatics.dna.dto.AlignmentRequest;
import com.bioinformatics.dna.dto.AlignmentResponse;
import com.bioinformatics.dna.model.AlgorithmType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AlignmentService {

    public AlignmentResponse analyze(AlignmentRequest request) {
        String seq1 = request.getSequence1().toUpperCase();
        String seq2 = request.getSequence2().toUpperCase();

        return request.getAlgorithmType() == AlgorithmType.GLOBAL
                ? runGlobalAlignment(seq1, seq2, request)
                : runLocalAlignment(seq1, seq2, request);
    }

    private AlignmentResponse runGlobalAlignment(String seq1, String seq2, AlignmentRequest request) {
        int rows = seq1.length() + 1;
        int cols = seq2.length() + 1;
        int[][] dp = new int[rows][cols];

        for (int i = 1; i < rows; i++) {
            dp[i][0] = dp[i - 1][0] + request.getGapPenalty();
        }
        for (int j = 1; j < cols; j++) {
            dp[0][j] = dp[0][j - 1] + request.getGapPenalty();
        }

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                int matchOrMismatch = seq1.charAt(i - 1) == seq2.charAt(j - 1)
                        ? request.getMatchScore()
                        : request.getMismatchScore();
                int diag = dp[i - 1][j - 1] + matchOrMismatch;
                int up = dp[i - 1][j] + request.getGapPenalty();
                int left = dp[i][j - 1] + request.getGapPenalty();
                dp[i][j] = Math.max(diag, Math.max(up, left));
            }
        }

        int i = seq1.length();
        int j = seq2.length();
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();
        List<AlignmentResponse.CellPoint> path = new ArrayList<>();

        while (i > 0 || j > 0) {
            path.add(new AlignmentResponse.CellPoint(i, j));
            if (i > 0 && j > 0) {
                int matchOrMismatch = seq1.charAt(i - 1) == seq2.charAt(j - 1)
                        ? request.getMatchScore()
                        : request.getMismatchScore();
                if (dp[i][j] == dp[i - 1][j - 1] + matchOrMismatch) {
                    aligned1.append(seq1.charAt(i - 1));
                    aligned2.append(seq2.charAt(j - 1));
                    i--;
                    j--;
                    continue;
                }
            }
            if (i > 0 && dp[i][j] == dp[i - 1][j] + request.getGapPenalty()) {
                aligned1.append(seq1.charAt(i - 1));
                aligned2.append('-');
                i--;
            } else {
                aligned1.append('-');
                aligned2.append(seq2.charAt(j - 1));
                j--;
            }
        }
        path.add(new AlignmentResponse.CellPoint(0, 0));

        return buildResponse(
                AlgorithmType.GLOBAL,
                seq1,
                seq2,
                dp,
                path,
                aligned1.reverse().toString(),
                aligned2.reverse().toString(),
                dp[seq1.length()][seq2.length()],
                "Global alignment compares full sequences end-to-end (Needleman-Wunsch)."
        );
    }

    private AlignmentResponse runLocalAlignment(String seq1, String seq2, AlignmentRequest request) {
        int rows = seq1.length() + 1;
        int cols = seq2.length() + 1;
        int[][] dp = new int[rows][cols];

        int bestScore = 0;
        int bestI = 0;
        int bestJ = 0;

        for (int i = 1; i < rows; i++) {
            for (int j = 1; j < cols; j++) {
                int matchOrMismatch = seq1.charAt(i - 1) == seq2.charAt(j - 1)
                        ? request.getMatchScore()
                        : request.getMismatchScore();
                int diag = dp[i - 1][j - 1] + matchOrMismatch;
                int up = dp[i - 1][j] + request.getGapPenalty();
                int left = dp[i][j - 1] + request.getGapPenalty();
                dp[i][j] = Math.max(0, Math.max(diag, Math.max(up, left)));

                if (dp[i][j] > bestScore) {
                    bestScore = dp[i][j];
                    bestI = i;
                    bestJ = j;
                }
            }
        }

        int i = bestI;
        int j = bestJ;
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();
        List<AlignmentResponse.CellPoint> path = new ArrayList<>();

        while (i > 0 && j > 0 && dp[i][j] > 0) {
            path.add(new AlignmentResponse.CellPoint(i, j));

            int matchOrMismatch = seq1.charAt(i - 1) == seq2.charAt(j - 1)
                    ? request.getMatchScore()
                    : request.getMismatchScore();
            if (dp[i][j] == dp[i - 1][j - 1] + matchOrMismatch) {
                aligned1.append(seq1.charAt(i - 1));
                aligned2.append(seq2.charAt(j - 1));
                i--;
                j--;
            } else if (dp[i][j] == dp[i - 1][j] + request.getGapPenalty()) {
                aligned1.append(seq1.charAt(i - 1));
                aligned2.append('-');
                i--;
            } else {
                aligned1.append('-');
                aligned2.append(seq2.charAt(j - 1));
                j--;
            }
        }
        path.add(new AlignmentResponse.CellPoint(i, j));

        return buildResponse(
                AlgorithmType.LOCAL,
                seq1,
                seq2,
                dp,
                path,
                aligned1.reverse().toString(),
                aligned2.reverse().toString(),
                bestScore,
                "Local alignment finds the highest-scoring subsequence region (Smith-Waterman)."
        );
    }

    private AlignmentResponse buildResponse(AlgorithmType type,
                                            String seq1,
                                            String seq2,
                                            int[][] matrix,
                                            List<AlignmentResponse.CellPoint> path,
                                            String aligned1,
                                            String aligned2,
                                            int score,
                                            String explanation) {
        String marker = buildMarker(aligned1, aligned2);
        double identity = calculateIdentity(aligned1, aligned2);

        Collections.reverse(path);

        AlignmentResponse response = new AlignmentResponse();
        response.setAlgorithmType(type);
        response.setNormalizedSequence1(seq1);
        response.setNormalizedSequence2(seq2);
        response.setAlignedSequence1(aligned1);
        response.setAlignedSequence2(aligned2);
        response.setAlignmentMarker(marker);
        response.setScore(score);
        response.setIdentityPercentage(identity);
        response.setScoringMatrix(toNestedList(matrix));
        response.setTracebackPath(path);
        response.setExplanation(explanation);
        return response;
    }

    private String buildMarker(String aligned1, String aligned2) {
        StringBuilder marker = new StringBuilder();
        for (int k = 0; k < Math.min(aligned1.length(), aligned2.length()); k++) {
            char c1 = aligned1.charAt(k);
            char c2 = aligned2.charAt(k);
            if (c1 == c2 && c1 != '-') {
                marker.append('|');
            } else if (c1 == '-' || c2 == '-') {
                marker.append(' ');
            } else {
                marker.append('.');
            }
        }
        return marker.toString();
    }

    private double calculateIdentity(String aligned1, String aligned2) {
        if (aligned1.isEmpty() || aligned2.isEmpty()) {
            return 0.0;
        }
        int length = Math.min(aligned1.length(), aligned2.length());
        int matches = 0;
        for (int i = 0; i < length; i++) {
            if (aligned1.charAt(i) == aligned2.charAt(i) && aligned1.charAt(i) != '-') {
                matches++;
            }
        }
        return (matches * 100.0) / length;
    }

    private List<List<Integer>> toNestedList(int[][] matrix) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] row : matrix) {
            List<Integer> values = new ArrayList<>();
            for (int value : row) {
                values.add(value);
            }
            result.add(values);
        }
        return result;
    }
}
