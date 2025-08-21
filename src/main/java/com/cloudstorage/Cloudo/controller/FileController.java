package com.cloudstorage.Cloudo.controller;

import com.cloudstorage.Cloudo.models.CloudFile;
import com.cloudstorage.Cloudo.models.User;
import com.cloudstorage.Cloudo.service.CloudFileService;
import com.cloudstorage.Cloudo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final CloudFileService fileService;
    private final UserService userService;

    @PostMapping("/upload")
    public ResponseEntity<CloudFile> uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        CloudFile uploadedFile = fileService.uploadFile(file, userService.getCurrentUser());
        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping
    public ResponseEntity<List<CloudFile>> getMyFiles() {
        List<CloudFile> files = fileService.getFilesByUser(userService.getCurrentUser());
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable UUID fileId) {
        try {
            User currentUser = userService.getCurrentUser();
            CloudFile file = fileService.getFile(fileId);

            // ✅ Ownership check
            if (!file.getOwner().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body("You are not authorized to delete this file.");
            }

            fileService.deleteFile(fileId);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete file: " + e.getMessage());
        }
    }

    @GetMapping("/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID fileId) throws IOException {
        User currentUser = userService.getCurrentUser();
        CloudFile file = fileService.getFile(fileId);

        // ✅ Ownership check
        if (!file.getOwner().getId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }

        Path path = Paths.get(file.getStoragePath());
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new IllegalArgumentException("File not found on disk");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getOriginalName() + "\"")
                .contentType(MediaType.parseMediaType(file.getFileType()))
                .body(resource);
    }
}
