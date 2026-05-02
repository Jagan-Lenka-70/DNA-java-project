package com.bioinformatics.dna.service;

import com.bioinformatics.dna.dto.AlignmentRequest;
import com.bioinformatics.dna.dto.AlignmentResponse;
import com.bioinformatics.dna.dto.HistoryItemDto;
import com.bioinformatics.dna.model.AnalysisRecord;
import com.bioinformatics.dna.model.UserAccount;
import com.bioinformatics.dna.repository.AnalysisRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalysisHistoryService {

    private final AnalysisRecordRepository analysisRecordRepository;

    public AnalysisHistoryService(AnalysisRecordRepository analysisRecordRepository) {
        this.analysisRecordRepository = analysisRecordRepository;
    }

    public void save(UserAccount user, AlignmentRequest request, AlignmentResponse response) {
        AnalysisRecord record = new AnalysisRecord();
        record.setUser(user);
        record.setAlgorithmType(response.getAlgorithmType());
        record.setSequence1(request.getSequence1().toUpperCase());
        record.setSequence2(request.getSequence2().toUpperCase());
        record.setScore(response.getScore());
        record.setIdentityPercentage(response.getIdentityPercentage());
        record.setAlignedSequence1(response.getAlignedSequence1());
        record.setAlignedSequence2(response.getAlignedSequence2());
        record.setAlignmentMarker(response.getAlignmentMarker());
        analysisRecordRepository.save(record);
    }

    public List<HistoryItemDto> getRecent(UserAccount user) {
        return analysisRecordRepository.findTop50ByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private HistoryItemDto toDto(AnalysisRecord record) {
        HistoryItemDto dto = new HistoryItemDto();
        dto.setId(record.getId());
        dto.setAlgorithmType(record.getAlgorithmType());
        dto.setSequence1(record.getSequence1());
        dto.setSequence2(record.getSequence2());
        dto.setScore(record.getScore());
        dto.setIdentityPercentage(record.getIdentityPercentage());
        dto.setAlignedSequence1(record.getAlignedSequence1());
        dto.setAlignedSequence2(record.getAlignedSequence2());
        dto.setAlignmentMarker(record.getAlignmentMarker());
        dto.setCreatedAt(record.getCreatedAt());
        return dto;
    }
}
