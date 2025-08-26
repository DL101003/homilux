package com.hoangloc.homilux.services;

import com.hoangloc.homilux.exceptions.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Service
public class FileService {

    @Value("${homilux.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectories(tmpDir.toPath());
                System.out.println("Tạo thư mục thành công với đường dẫn: " + tmpDir.toPath());
            } catch (IOException e) {
                throw new StorageException("Tạo thư mục thất bại: " + folder, e);
            }
        } else {
            System.out.println("Bỏ qua tạo mới folder, folder đã tồn tại");
        }
    }

    public List<String> store(List<MultipartFile> files, String folder) {
        if (files == null || files.isEmpty()) {
            throw new StorageException("Không có file nào được thêm vào");
        }

        return files.stream()
                .map(file -> {
                    try {
                        return storeSingleFile(file, folder);
                    } catch (URISyntaxException | IOException e) {
                        throw new StorageException("Có lỗi khi up file: " + file.getOriginalFilename(), e);
                    }
                })
                .toList();
    }

    private String storeSingleFile(MultipartFile file, String folder) throws URISyntaxException, IOException {
        if (file == null || file.isEmpty()) {
            throw new StorageException("File rỗng");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
        boolean isValid = allowedExtensions.stream().anyMatch(item -> {
            assert fileName != null;
            return fileName.toLowerCase().endsWith(item);
        });

        if (!isValid) {
            throw new StorageException("Định dạng file không hợp lệ");
        }

        createDirectory(baseURI + folder);

        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        URI uri = new URI(baseURI + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return finalName;
    }

}