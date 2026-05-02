package com.bioinformatics.dna.repository;

import com.bioinformatics.dna.model.AnalysisRecord;
import com.bioinformatics.dna.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRecordRepository extends JpaRepository<AnalysisRecord, Long> {
    List<AnalysisRecord> findTop50ByUserOrderByCreatedAtDesc(UserAccount user);
}
