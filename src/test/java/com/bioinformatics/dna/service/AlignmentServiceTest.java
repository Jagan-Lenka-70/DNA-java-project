package com.bioinformatics.dna.service;

import com.bioinformatics.dna.dto.AlignmentRequest;
import com.bioinformatics.dna.dto.AlignmentResponse;
import com.bioinformatics.dna.model.AlgorithmType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AlignmentServiceTest {

    private final AlignmentService alignmentService = new AlignmentService();

    @Test
    void shouldComputeGlobalAlignment() {
        AlignmentRequest request = new AlignmentRequest();
        request.setSequence1("ACGT");
        request.setSequence2("ACCT");
        request.setAlgorithmType(AlgorithmType.GLOBAL);
        request.setMatchScore(2);
        request.setMismatchScore(-1);
        request.setGapPenalty(-2);

        AlignmentResponse response = alignmentService.analyze(request);

        Assertions.assertEquals(AlgorithmType.GLOBAL, response.getAlgorithmType());
        Assertions.assertFalse(response.getAlignedSequence1().isEmpty());
        Assertions.assertFalse(response.getAlignedSequence2().isEmpty());
    }

    @Test
    void shouldComputeLocalAlignment() {
        AlignmentRequest request = new AlignmentRequest();
        request.setSequence1("TACGGT");
        request.setSequence2("ACGG");
        request.setAlgorithmType(AlgorithmType.LOCAL);
        request.setMatchScore(2);
        request.setMismatchScore(-1);
        request.setGapPenalty(-2);

        AlignmentResponse response = alignmentService.analyze(request);

        Assertions.assertEquals(AlgorithmType.LOCAL, response.getAlgorithmType());
        Assertions.assertTrue(response.getScore() > 0);
    }
}
