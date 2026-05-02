package com.bioinformatics.dna.controller;

import com.bioinformatics.dna.dto.AlignmentRequest;
import com.bioinformatics.dna.dto.AlignmentResponse;
import com.bioinformatics.dna.dto.BatchReportDto;
import com.bioinformatics.dna.dto.HistoryItemDto;
import com.bioinformatics.dna.model.AlgorithmType;
import com.bioinformatics.dna.model.UserAccount;
import com.bioinformatics.dna.service.AnalysisHistoryService;
import com.bioinformatics.dna.service.AlignmentService;
import com.bioinformatics.dna.service.AuthService;
import com.bioinformatics.dna.service.FastaService;
import com.bioinformatics.dna.service.TokenExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/alignment")
public class AlignmentController {

    private final AlignmentService alignmentService;
    private final AuthService authService;
    private final TokenExtractor tokenExtractor;
    private final AnalysisHistoryService analysisHistoryService;
    private final FastaService fastaService;

    public AlignmentController(AlignmentService alignmentService,
                               AuthService authService,
                               TokenExtractor tokenExtractor,
                               AnalysisHistoryService analysisHistoryService,
                               FastaService fastaService) {
        this.alignmentService = alignmentService;
        this.authService = authService;
        this.tokenExtractor = tokenExtractor;
        this.analysisHistoryService = analysisHistoryService;
        this.fastaService = fastaService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AlignmentResponse> analyze(@Valid @RequestBody AlignmentRequest request, HttpServletRequest servletRequest) {
        UserAccount user = authService.requireUser(tokenExtractor.extract(servletRequest));
        AlignmentResponse response = alignmentService.analyze(request);
        analysisHistoryService.save(user, request, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<HistoryItemDto>> history(HttpServletRequest request) {
        UserAccount user = authService.requireUser(tokenExtractor.extract(request));
        return ResponseEntity.ok(analysisHistoryService.getRecent(user));
    }

    @PostMapping("/batch-fasta")
    public ResponseEntity<BatchReportDto> batchFasta(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("algorithmType") AlgorithmType algorithmType,
                                                     @RequestParam(value = "matchScore", defaultValue = "2") int matchScore,
                                                     @RequestParam(value = "mismatchScore", defaultValue = "-1") int mismatchScore,
                                                     @RequestParam(value = "gapPenalty", defaultValue = "-2") int gapPenalty,
                                                     HttpServletRequest request) {
        authService.requireUser(tokenExtractor.extract(request));
        return ResponseEntity.ok(fastaService.buildBatchReport(file, algorithmType, matchScore, mismatchScore, gapPenalty));
    }
}
