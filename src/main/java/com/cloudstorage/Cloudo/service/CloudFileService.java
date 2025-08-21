package com.cloudstorage.Cloudo.service;

import com.cloudstorage.Cloudo.models.CloudFile;
import com.cloudstorage.Cloudo.models.User;
import com.cloudstorage.Cloudo.Repo.CloudFileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudFileService {

    private final CloudFileRepo cloudFileRepo;

    private static final String FILE_STORAGE_BASE_PATH = "uploads/";

    /**
     * Uploads a file to the user's directory and saves metadata to DB
     */
    public CloudFile uploadFile(MultipartFile multipartFile, User user) throws IOException {
        // Create user-specific folder if it doesn't exist
        Path userUploadDir = Paths.get(FILE_STORAGE_BASE_PATH + user.getUsername());
        Files.createDirectories(userUploadDir);

        // Generate unique filename
        String originalFilename = multipartFile.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String storedName = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);

        Path filePath = userUploadDir.resolve(storedName);
        Files.copy(multipartFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Build CloudFile entity
        CloudFile file = CloudFile.builder()
                .originalName(originalFilename)
                .storedName(storedName)
                .fileType(multipartFile.getContentType())
                .size(multipartFile.getSize())
                .storagePath(filePath.toString())
                .category("uncategorized") // To be enhanced with AI later
                .isPublic(false)
                .owner(user)
                .build();

        return cloudFileRepo.save(file);
    }

    /**
     * Retrieves all files uploaded by a user
     */
    public List<CloudFile> getFilesByUser(User user) {
        return cloudFileRepo.findByOwner(user);
    }

    /**
     * Deletes a file by its UUID — both physical and DB record
     */
    public void deleteFile(UUID fileId) throws IOException {
        CloudFile file = cloudFileRepo.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Delete the actual file
        Files.deleteIfExists(Paths.get(file.getStoragePath()));

        // Remove metadata from DB
        cloudFileRepo.deleteById(fileId);
    }

    // Utility: Get file extension
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
    public CloudFile getFile(UUID fileId) {
        return cloudFileRepo.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

}
