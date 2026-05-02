package com.bioinformatics.dna.config;

import com.bioinformatics.dna.model.AlgorithmType;
import com.bioinformatics.dna.model.AnalysisRecord;
import com.bioinformatics.dna.model.UserAccount;
import com.bioinformatics.dna.repository.AnalysisRecordRepository;
import com.bioinformatics.dna.repository.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final AnalysisRecordRepository analysisRecordRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DemoDataInitializer(UserAccountRepository userAccountRepository,
                               AnalysisRecordRepository analysisRecordRepository) {
        this.userAccountRepository = userAccountRepository;
        this.analysisRecordRepository = analysisRecordRepository;
    }

    @Override
    public void run(String... args) {
        UserAccount demo = userAccountRepository.findByUsername("demo").orElseGet(() -> {
            UserAccount user = new UserAccount();
            user.setUsername("demo");
            user.setEmail("demo@dna.com");
            user.setPasswordHash(encoder.encode("demo123"));
            return userAccountRepository.save(user);
        });

        if (analysisRecordRepository.countByUser(demo) == 0) {
            analysisRecordRepository.save(createRecord(
                    demo,
                    AlgorithmType.GLOBAL,
                    "ACGTTGAC",
                    "ACTTGACC",
                    "ACGTTGAC-",
                    "||.||||| ",
                    "ACTTGACC-",
                    10,
                    77.77
            ));
            analysisRecordRepository.save(createRecord(
                    demo,
                    AlgorithmType.LOCAL,
                    "TTGACCGTACGAT",
                    "GGACCGTTCG",
                    "GACCGTACG",
                    "||||||.||",
                    "GACCGTTCG",
                    14,
                    88.88
            ));
        }
    }

    private AnalysisRecord createRecord(UserAccount user,
                                        AlgorithmType type,
                                        String seq1,
                                        String seq2,
                                        String aligned1,
                                        String marker,
                                        String aligned2,
                                        int score,
                                        double identity) {
        AnalysisRecord record = new AnalysisRecord();
        record.setUser(user);
        record.setAlgorithmType(type);
        record.setSequence1(seq1);
        record.setSequence2(seq2);
        record.setAlignedSequence1(aligned1);
        record.setAlignmentMarker(marker);
        record.setAlignedSequence2(aligned2);
        record.setScore(score);
        record.setIdentityPercentage(identity);
        return record;
    }
}
