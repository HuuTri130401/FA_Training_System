package com.fptacademy.training.web;

import com.fptacademy.training.service.FileStorageService;
import com.fptacademy.training.service.dto.FileStorageDto;
import com.fptacademy.training.web.api.FileStorageResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class FileStorageImpl implements FileStorageResource {

    private final FileStorageService storageService;

    @Override
    public ResponseEntity<List<FileStorageDto>> getAllFiles() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(storageService.getAllFile());
    }

    @Override
    public ResponseEntity<?> handleFileUpload(MultipartFile file, String description) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(storageService.handleFileUpload(file, description));
    }
}
