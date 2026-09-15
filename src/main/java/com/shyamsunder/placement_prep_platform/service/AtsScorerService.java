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

    // Synonym dictionary for placement-relevant terminology
    private static final Map<String, List<String>> SYNONYMS = new LinkedHashMap<>();
    static {
        SYNONYMS.put("spring boot", List.of("spring boot", "springboot", "spring-boot", "spring framework"));
        SYNONYMS.put("javascript", List.of("javascript", "js", "ecmascript"));
        SYNONYMS.put("typescript", List.of("typescript", "ts"));
        SYNONYMS.put("sql", List.of("sql", "sql db", "rdbms", "relational database"));
        SYNONYMS.put("rest api", List.of("rest api", "restful api", "rest apis", "restful apis", "rest services"));
        SYNONYMS.put("react", List.of("react", "reactjs", "react.js"));
        SYNONYMS.put("node", List.of("node", "nodejs", "node.js"));
        SYNONYMS.put("docker", List.of("docker", "dockerized", "containers"));
        SYNONYMS.put("kubernetes", List.of("kubernetes", "k8s"));
        SYNONYMS.put("aws", List.of("aws", "aws cloud", "amazon web services"));
        SYNONYMS.put("c++", List.of("c++", "cpp"));
        SYNONYMS.put("c#", List.of("c#", "csharp", ".net"));
        SYNONYMS.put("git", List.of("git", "github", "gitlab"));
        SYNONYMS.put("mongodb", List.of("mongodb", "mongo"));
        SYNONYMS.put("postgresql", List.of("postgresql", "postgres"));
    }

    // Technical categories for breadth evaluation (25%)
    private static final Map<String, List<String>> TECH_CATEGORIES = Map.of(
            "Languages", List.of("java", "python", "c++", "c#", "javascript", "typescript", "go", "c"),
            "WebFrameworks", List.of("spring boot", "spring", "react", "angular", "node", "express", "html", "css"),
            "Databases", List.of("sql", "mysql", "postgresql", "mongodb", "hibernate", "jpa", "redis"),
            "CoreCS", List.of("data structures", "algorithms", "system design", "oops", "dbms", "operating systems"),
            "DevOpsTools", List.of("git", "docker", "kubernetes", "aws", "ci/cd", "linux", "maven")
    );

    // Experience action keywords (15%)
    private static final List<String> EXPERIENCE_KEYWORDS = List.of(
            "intern", "internship", "experience", "developed", "implemented", "built",
            "designed", "production", "delivered", "collaborated", "engineered", "maintained", "tested"
    );

    // Education keywords (10%)
    private static final List<String> EDUCATION_KEYWORDS = List.of(
            "bachelor", "btech", "b.tech", "be", "b.e", "mtech", "mca", "degree",
            "computer science", "engineering", "university", "college", "gpa", "cgpa"
    );

    // Project keywords (10%)
    private static final List<String> PROJECT_KEYWORDS = List.of(
            "project", "github", "repository", "full-stack", "frontend", "backend",
            "database", "api", "rest api", "architecture", "application"
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

        // 1. Keyword / Skill Match (40%)
        List<String> targetSkills = parseTargetSkills(request.getJobDescription());
        List<String> matchedSkills = new ArrayList<>();
        List<String> missingSkills = new ArrayList<>();

        for (String skill : targetSkills) {
            if (matchesWithSynonyms(combinedContent, skill.toLowerCase().trim())) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        int keywordScore = 0;
        if (!targetSkills.isEmpty()) {
            keywordScore = (int) Math.round(((double) matchedSkills.size() / targetSkills.size()) * 40.0);
            keywordScore = Math.min(40, Math.max(0, keywordScore));
        }

        // 2. Technical Breadth Evaluation (25%) - 5 points per category
        int techCategoriesMatched = 0;
        for (List<String> skillsInCat : TECH_CATEGORIES.values()) {
            boolean hasMatch = skillsInCat.stream().anyMatch(s -> matchesWithSynonyms(combinedContent, s));
            if (hasMatch) {
                techCategoriesMatched++;
            }
        }
        int technicalScore = techCategoriesMatched * 5; // Up to 25

        // 3. Experience Keywords (15%) - 3 points per matched keyword, max 15
        long expMatches = EXPERIENCE_KEYWORDS.stream().filter(k -> matchesKeyword(combinedContent, k)).count();
        int experienceScore = (int) Math.min(15, expMatches * 3);

        // 4. Education Keywords (10%) - 3.5 points per matched keyword, max 10
        long eduMatches = EDUCATION_KEYWORDS.stream().filter(k -> matchesKeyword(combinedContent, k)).count();
        int educationScore = (int) Math.min(10, Math.round(eduMatches * 3.5));

        // 5. Project Keywords (10%) - 3.5 points per matched keyword, max 10
        long projMatches = PROJECT_KEYWORDS.stream().filter(k -> matchesKeyword(combinedContent, k)).count();
        int projectScore = (int) Math.min(10, Math.round(projMatches * 3.5));

        // If resume text is completely empty, all scores remain 0
        if (extractedText.trim().isEmpty() && (resume.getFileName() == null || resume.getFileName().trim().isEmpty())) {
            keywordScore = 0;
            technicalScore = 0;
            experienceScore = 0;
            educationScore = 0;
            projectScore = 0;
        }

        int totalScore = keywordScore + technicalScore + experienceScore + educationScore + projectScore;
        totalScore = Math.min(100, Math.max(0, totalScore));

        Map<String, Integer> scoreBreakdown = new LinkedHashMap<>();
        scoreBreakdown.put("keywordMatch", keywordScore);
        scoreBreakdown.put("technicalBreadth", technicalScore);
        scoreBreakdown.put("experience", experienceScore);
        scoreBreakdown.put("education", educationScore);
        scoreBreakdown.put("projects", projectScore);

        List<String> recommendations = generateRecommendations(missingSkills, totalScore, scoreBreakdown);

        return AtsAnalysisResponse.builder()
                .resumeId(resume.getId())
                .fileName(resume.getFileName())
                .score(totalScore)
                .matchedSkills(matchedSkills)
                .missingSkills(missingSkills)
                .scoreBreakdown(scoreBreakdown)
                .recommendations(recommendations)
                .build();
    }

    private boolean matchesWithSynonyms(String content, String skill) {
        if (content == null || skill == null || skill.isEmpty()) {
            return false;
        }

        // Direct match with boundary
        if (matchesKeyword(content, skill)) {
            return true;
        }

        // Check synonym map
        List<String> synonyms = SYNONYMS.get(skill);
        if (synonyms != null) {
            for (String syn : synonyms) {
                if (matchesKeyword(content, syn)) {
                    return true;
                }
            }
        }

        // Reverse check: if skill is a synonym variant of an existing key
        for (Map.Entry<String, List<String>> entry : SYNONYMS.entrySet()) {
            if (entry.getValue().contains(skill) || entry.getKey().equalsIgnoreCase(skill)) {
                for (String syn : entry.getValue()) {
                    if (matchesKeyword(content, syn)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean matchesKeyword(String content, String keyword) {
        if (content == null || keyword == null || keyword.isEmpty()) {
            return false;
        }
        // Non-alphanumeric boundary match avoids false positives like 'c' in 'docker'
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
            if (matchesWithSynonyms(lowerJd, skill)) {
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

    private List<String> generateRecommendations(List<String> missingSkills, int totalScore, Map<String, Integer> breakdown) {
        List<String> recs = new ArrayList<>();

        if (totalScore < 50) {
            recs.add("Low Match: Resume needs significant alignment with the target job description.");
        } else if (totalScore < 80) {
            recs.add("Moderate Match: Good baseline; enhance key skill areas to increase placement shortlisting chances.");
        } else {
            recs.add("Strong Match: Excellent alignment across technical skills and experience credentials.");
        }

        if (!missingSkills.isEmpty()) {
            List<String> previewMissing = missingSkills.subList(0, Math.min(6, missingSkills.size()));
            recs.add("Add missing key terms to your skills section: " + String.join(", ", previewMissing));
        }

        if (breakdown.getOrDefault("technicalBreadth", 0) < 15) {
            recs.add("Broaden core technical stack: Ensure your resume showcases Databases, Core CS, and DevOps/Git tooling.");
        }

        if (breakdown.getOrDefault("experience", 0) < 6) {
            recs.add("Strengthen experience bullet points using strong action verbs (e.g., 'developed', 'implemented', 'designed', 'tested').");
        }

        if (breakdown.getOrDefault("projects", 0) < 6) {
            recs.add("Highlight 2-3 full-stack or backend projects with GitHub repository links and architecture details.");
        }

        return recs;
    }
}
