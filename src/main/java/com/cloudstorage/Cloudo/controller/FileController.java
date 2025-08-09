package com.cloudstorage.Cloudo.controller;

import com.cloudstorage.Cloudo.models.CloudFile;
import com.cloudstorage.Cloudo.service.CloudFileService;
import com.cloudstorage.Cloudo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            fileService.deleteFile(fileId);
            return ResponseEntity.ok("File deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete file: " + e.getMessage());
        }
    }
}

