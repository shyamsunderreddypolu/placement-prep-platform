package com.shyamsunder.placement_prep_platform.service;

import com.shyamsunder.placement_prep_platform.exception.FileValidationException;
import com.shyamsunder.placement_prep_platform.exception.ResourceNotFoundException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipInputStream;

@Service
@ConditionalOnProperty(name = "aws.s3.enabled", havingValue = "false", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".pdf", ".docx");
    private static final Set<String> DANGEROUS_EXTENSIONS = Set.of(
            ".exe", ".sh", ".jsp", ".php", ".js", ".html", ".bat", ".cmd", ".jar", ".vbs"
    );
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    @Override
    public String uploadFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        }
        
        // Prevent path traversal and generate secure UUID filename
        String secureFileName = UUID.randomUUID().toString() + extension;

        try {
            Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            Path targetPath = path.resolve(secureFileName).normalize();
            if (!targetPath.startsWith(path)) {
                throw new FileValidationException("Path traversal attempt detected");
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return secureFileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file locally: " + e.getMessage(), e);
        }
    }

    @Override
    public Resource loadAsResource(String fileName) {
        try {
            // Strip any legacy /uploads/ prefix if present
            String cleanName = fileName;
            if (cleanName.startsWith("/uploads/")) {
                cleanName = cleanName.substring("/uploads/".length());
            }

            Path basePath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = basePath.resolve(cleanName).normalize();

            if (!filePath.startsWith(basePath)) {
                throw new FileValidationException("Invalid file path");
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found or not readable: " + cleanName);
            }
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("Could not read file: " + fileName);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileValidationException("Uploaded file is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileValidationException("File exceeds maximum allowed size of 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileValidationException("File name is missing or invalid");
        }

        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new FileValidationException("File name contains invalid path traversal sequences");
        }

        String lowerName = originalFilename.toLowerCase();
        for (String dangerous : DANGEROUS_EXTENSIONS) {
            if (lowerName.endsWith(dangerous)) {
                throw new FileValidationException("File type not permitted: " + dangerous);
            }
        }

        String extension = "";
        if (lowerName.contains(".")) {
            extension = lowerName.substring(lowerName.lastIndexOf("."));
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileValidationException("Only PDF and DOCX files are allowed. Given: " + extension);
        }

        String contentType = file.getContentType();
        if (contentType != null && !contentType.equalsIgnoreCase("application/octet-stream")) {
            boolean validMime = ALLOWED_MIME_TYPES.stream().anyMatch(t -> t.equalsIgnoreCase(contentType));
            if (!validMime) {
                throw new FileValidationException("Invalid MIME type: " + contentType);
            }
        }

        validateFileMagicBytes(file, extension);
    }

    private void validateFileMagicBytes(MultipartFile file, String extension) {
        try {
            byte[] bytes = file.getBytes();
            if (bytes.length < 4) {
                throw new FileValidationException("File is corrupted or too small");
            }

            if (extension.equals(".pdf")) {
                // PDF magic bytes: %PDF (0x25, 0x50, 0x44, 0x46)
                if (bytes[0] != 0x25 || bytes[1] != 0x50 || bytes[2] != 0x44 || bytes[3] != 0x46) {
                    throw new FileValidationException("File content does not match PDF signature");
                }
                // Verify PDF parseability with PDFBox
                try (PDDocument doc = PDDocument.load(new ByteArrayInputStream(bytes))) {
                    if (doc.isEncrypted()) {
                        throw new FileValidationException("Encrypted PDF files are not supported");
                    }
                } catch (IOException e) {
                    throw new FileValidationException("Corrupted or unreadable PDF document");
                }
            } else if (extension.equals(".docx")) {
                // DOCX magic bytes (ZIP archive): PK.. (0x50, 0x4B, 0x03, 0x04)
                if (bytes[0] != 0x50 || bytes[1] != 0x4B || bytes[2] != 0x03 || bytes[3] != 0x04) {
                    throw new FileValidationException("File content does not match DOCX signature");
                }
                // Verify zip structure contains [Content_Types].xml or word/
                boolean hasWordOrContentTypes = false;
                try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes))) {
                    java.util.zip.ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null) {
                        if (entry.getName().startsWith("word/") || entry.getName().contains("[Content_Types].xml")) {
                            hasWordOrContentTypes = true;
                            break;
                        }
                    }
                } catch (IOException e) {
                    throw new FileValidationException("Corrupted DOCX archive");
                }
                if (!hasWordOrContentTypes) {
                    throw new FileValidationException("Invalid DOCX format");
                }
            }
        } catch (IOException e) {
            throw new FileValidationException("Failed to read file content for validation: " + e.getMessage());
        }
    }
}
