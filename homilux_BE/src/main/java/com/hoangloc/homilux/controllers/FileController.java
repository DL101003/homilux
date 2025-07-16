package com.hoangloc.homilux.controllers;

import com.hoangloc.homilux.dtos.UploadFileDto;
import com.hoangloc.homilux.exceptions.StorageException;
import com.hoangloc.homilux.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files")
    public ResponseEntity<List<UploadFileDto>> uploadFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("folder") String folder) throws StorageException {

        if (files == null || files.length == 0) {
            throw new StorageException("No files were uploaded. Please try again.");
        }

        List<String> uploadResults = fileService.store(List.of(files), folder);
        List<UploadFileDto> response = uploadResults.stream()
                .map(result -> new UploadFileDto(result, Instant.now()))
                .toList();

        return ResponseEntity.ok(response) ;
    }


}