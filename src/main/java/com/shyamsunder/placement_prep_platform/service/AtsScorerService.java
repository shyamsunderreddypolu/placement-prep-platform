package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisRequest;
import com.shyamsunder.placement_prep_platform.dto.AtsAnalysisResponse;
import com.shyamsunder.placement_prep_platform.entity.Resume;
import com.shyamsunder.placement_prep_platform.entity.User;
import com.shyamsunder.placement_prep_platform.exception.ForbiddenException;
import com.shyamsunder.placement_prep_platform.exception.ResourceNotFoundException;
import com.shyamsunder.placement_prep_platform.repository.ResumeRepository;
import com.shyamsunder.placement_prep_platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userEmail));

        Resume resume = resumeRepository.findById(request.getResumeId())
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with ID: " + request.getResumeId()));

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("Unauthorized access to resume");
        }

        String extractedText = extractTextFromResume(resume);
        String combinedContent = (extractedText + " " + (resume.getFileName() != null ? resume.getFileName() : "")).toLowerCase();

        List<String> targetSkills = parseTargetSkills(request.getJobDescription());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : targetSkills) {
            String lowerSkill = skill.toLowerCase().trim();
            if (matchesKeyword(combinedContent, lowerSkill)) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        // True calculated score without artificial manufacturing or min 35 cap
        int score = 0;
        if (!targetSkills.isEmpty()) {
            score = (int) Math.round(((double) matchedSkills.size() / targetSkills.size()) * 100);
            score = Math.min(100, Math.max(0, score));
        }

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

    private boolean matchesKeyword(String content, String keyword) {
        if (content == null || keyword == null || keyword.isEmpty()) {
            return false;
        }
        // Use non-alphanumeric boundary checks to avoid false positives (e.g. 'c' matching in 'docker')
        String regex = "(?<![a-zA-Z0-9])" + Pattern.quote(keyword) + "(?![a-zA-Z0-9])";
        return Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(content).find();
    }

    private String extractTextFromResume(Resume resume) {
        String fileUrl = resume.getFileUrl();
        if (fileUrl == null) return "";

        String fileName = fileUrl;
        if (fileUrl.startsWith("/uploads/")) {
            fileName = fileUrl.substring("/uploads/".length());
        }

        List<Path> candidatePaths = List.of(
                Paths.get(uploadDir != null ? uploadDir : "uploads", fileName),
                Paths.get("./uploads", fileName),
                Paths.get(System.getProperty("user.dir"), "uploads", fileName)
        );

        File file = null;
        for (Path path : candidatePaths) {
            if (Files.exists(path)) {
                file = path.toFile();
                break;
            }
        }

        if (file != null && file.exists()) {
            String lowerName = fileName.toLowerCase();
            if (lowerName.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(file)) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(document);
                } catch (IOException e) {
                    log.warn("Failed to extract text from PDF resume: {}", e.getMessage());
                    return "";
                }
            } else if (lowerName.endsWith(".txt") || lowerName.endsWith(".doc") || lowerName.endsWith(".docx")) {
                try {
                    return Files.readString(file.toPath());
                } catch (IOException e) {
                    log.warn("Failed to extract text from document: {}", e.getMessage());
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
            if (matchesKeyword(lowerJd, skill.toLowerCase())) {
                skills.add(skill);
            }
        }

        String[] tokens = jobDescription.split("[,\\n;]+");
        for (String token : tokens) {
            String trimmed = token.trim();
            if (trimmed.length() >= 2 && trimmed.length() <= 35) {
                skills.add(trimmed);
            }
        }

        return skills.isEmpty() ? COMMON_PLACEMENT_SKILLS : new ArrayList<>(skills);
    }

    private List<String> generateRecommendations(List<String> missingSkills, int score) {
        List<String> recs = new ArrayList<>();

        if (score < 40) {
            recs.add("Low Match: Resume is missing essential skills specified in the target job description.");
        } else if (score < 75) {
            recs.add("Moderate Match: Consider adding projects or coursework demonstrating missing technologies.");
        } else {
            recs.add("Strong Match: Resume aligns well with target placement requirements.");
        }

        if (!missingSkills.isEmpty()) {
            recs.add("Add missing key terms to your skills section: " + String.join(", ", missingSkills));
        }

        return recs;
    }
}
