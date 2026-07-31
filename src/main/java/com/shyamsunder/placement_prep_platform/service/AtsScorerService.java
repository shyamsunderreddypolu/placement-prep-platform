package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisRequest;
import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.repository.ResumeRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AtsScorerService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final List<String> COMMON_PLACEMENT_SKILLS = List.of(
            "java", "spring boot", "mysql", "react", "rest api", "python",
            "data structures", "algorithms", "docker", "git", "microservices",
            "system design", "javascript", "c++", "aws", "html", "css", "sql", "hibernate"
    );

    public AtsAnalysisResponse analyzeResume(AtsAnalysisRequest request) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));

        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new RuntimeException("Resume not found with ID: " + request.getResumeId()));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized access to resume");
        }

        String extractedText = extractTextFromResume(resume);
        String combinedContent = (extractedText + " " + resume.getFileName()).toLowerCase();

        List<String> targetSkills = parseTargetSkills(request.getJobDescription());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : targetSkills) {
            if (combinedContent.contains(skill.toLowerCase())) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        int score = targetSkills.isEmpty() ? 0 : (int) Math.round(((double) matchedSkills.size() / targetSkills.size()) * 100);

        List<String> recommendations = generateRecommendations(missingSkills, score);

        return AtsAnalysisResponse.builder()
                .resumeId(resume.getId())
                .fileName(resume.getFileName())
                .score(score)
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .recommendations(recommendations)
                .build();
    }

    private String extractTextFromResume(Resume resume) {
        String fileUrl = resume.getFileUrl();
        if (fileUrl != null && fileUrl.startsWith("/uploads/")) {
            String fileName = fileUrl.substring("/uploads/".length());
            Path path = Paths.get(uploadDir, fileName);
            File file = path.toFile();
            if (file.exists() && fileName.toLowerCase().endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(file)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                } catch (IOException e) {
                    return "";
                }
            }
        }
        return "";
    }

    private List<String> parseTargetSkills(String jobDescription) {
        if (jobDescription == null || jobDescription.trim().isEmpty()) {
            return COMMON_PLACEMENT_SKILLS;
        }

        Set<String> skills = new LinkedHashSet<>();
        String lowerJd = jobDescription.toLowerCase();

        for (String skill : COMMON_PLACEMENT_SKILLS) {
            if (lowerJd.contains(skill)) {
                skills.add(skill);
            }
        }

        if (skills.isEmpty()) {
            return COMMON_PLACEMENT_SKILLS;
        }

        return new ArrayList<>(skills);
    }

    private List<String> generateRecommendations(List<String> missingSkills, int score) {
        List<String> recs = new ArrayList<>();

        if (score < 50) {
            recs.add("High Priority: Resume is missing several core placement skills. Update experience bullet points.");
        } else if (score < 80) {
            recs.add("Moderate Match: Consider adding projects demonstrating missing technologies.");
        } else {
            recs.add("Strong Match: Resume aligns well with target placement requirements.");
        }

        if (!missingSkills.isEmpty()) {
            recs.add("Add missing key terms to your skills section: " + String.join(", ", missingSkills));
        }

        return recs;
    }
}
