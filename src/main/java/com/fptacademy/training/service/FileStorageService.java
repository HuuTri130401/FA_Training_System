package com.fptacademy.training.service;

import com.fptacademy.training.domain.FileStorage;
import com.fptacademy.training.repository.FileStorageRepository;
import com.fptacademy.training.service.dto.FileStorageDto;
import com.fptacademy.training.service.mapper.FileStorageMapper;
import com.fptacademy.training.service.util.S3UploadFileUtil;
import com.fptacademy.training.web.FileStorageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class FileStorageService {
    private final FileStorageRepository fileStorageRepository;
    private final FileStorageMapper storageMapper;
    private final S3UploadFileUtil s3UploadFileUtil;
    public List<FileStorageDto> getAllFile() {
        return storageMapper.toDtos(fileStorageRepository.findAll());
    }

    public FileStorageDto addFile(FileStorage fileStorage){
        return storageMapper.toDto(fileStorageRepository.save(fileStorage));
    }

    public ResponseEntity<?> handleFileUpload(MultipartFile file, String description) {
        return (ResponseEntity<?>) s3UploadFileUtil.handleFileUpload(file, description);
    }
}
