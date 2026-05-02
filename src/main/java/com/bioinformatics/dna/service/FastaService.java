package com.bioinformatics.dna.service;

import com.bioinformatics.dna.dto.AlignmentRequest;
import com.bioinformatics.dna.dto.AlignmentResponse;
import com.bioinformatics.dna.dto.BatchReportDto;
import com.bioinformatics.dna.exception.ApiException;
import com.bioinformatics.dna.model.AlgorithmType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class FastaService {

    private final AlignmentService alignmentService;

    public FastaService(AlignmentService alignmentService) {
        this.alignmentService = alignmentService;
    }

    public BatchReportDto buildBatchReport(MultipartFile file, AlgorithmType algorithmType, int match, int mismatch, int gap) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please upload a FASTA file.");
        }
        List<FastaEntry> entries = parseFasta(file);
        if (entries.size() < 2) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "FASTA must contain at least 2 sequences.");
        }

        FastaEntry reference = entries.get(0);
        BatchReportDto report = new BatchReportDto();
        report.setAlgorithmType(algorithmType);
        report.setReferenceHeader(reference.header);
        report.setReferenceSequence(reference.sequence);

        List<BatchReportDto.BatchItem> items = new ArrayList<>();
        for (int i = 1; i < entries.size(); i++) {
            FastaEntry entry = entries.get(i);
            AlignmentRequest request = new AlignmentRequest();
            request.setSequence1(reference.sequence);
            request.setSequence2(entry.sequence);
            request.setAlgorithmType(algorithmType);
            request.setMatchScore(match);
            request.setMismatchScore(mismatch);
            request.setGapPenalty(gap);

            AlignmentResponse response = alignmentService.analyze(request);
            BatchReportDto.BatchItem item = new BatchReportDto.BatchItem();
            item.setHeader(entry.header);
            item.setSequence(entry.sequence);
            item.setScore(response.getScore());
            item.setIdentityPercentage(response.getIdentityPercentage());
            item.setAlignedSequence1(response.getAlignedSequence1());
            item.setAlignmentMarker(response.getAlignmentMarker());
            item.setAlignedSequence2(response.getAlignedSequence2());
            items.add(item);
        }
        report.setResults(items);
        return report;
    }

    private List<FastaEntry> parseFasta(MultipartFile file) {
        List<FastaEntry> entries = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            String header = null;
            StringBuilder sequence = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (trimmed.startsWith(">")) {
                    if (header != null) {
                        entries.add(toEntry(header, sequence.toString()));
                        sequence = new StringBuilder();
                    }
                    header = trimmed.substring(1).trim();
                } else {
                    sequence.append(trimmed.toUpperCase().replaceAll("[^ACGT]", ""));
                }
            }
            if (header != null) {
                entries.add(toEntry(header, sequence.toString()));
            }
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Failed to read FASTA file.");
        }
        return entries;
    }

    private FastaEntry toEntry(String header, String sequence) {
        if (sequence.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "One FASTA sequence is empty.");
        }
        return new FastaEntry(header.isBlank() ? "Untitled" : header, sequence);
    }

    private record FastaEntry(String header, String sequence) {
    }
}
