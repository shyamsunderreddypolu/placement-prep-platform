package com.shyamsunder.placement_prep_platform.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String uploadFile(MultipartFile file);
    Resource loadAsResource(String fileName);
}
