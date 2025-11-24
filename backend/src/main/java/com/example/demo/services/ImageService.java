package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
public class ImageService {
    private static final long MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png");


    public String saveImage(MultipartFile file) {
        try {
            Path path = Paths.get("images");  // thư mục lưu ảnh
            Files.createDirectories(path);    // tạo thư mục nếu chưa có

            // Tạo tên file kiểu timestamp.extension
            String fileName = String.format("%s.%s",
                    new Date().getTime(),
                    file.getContentType().split("/")[1]);

            // Lưu file vào thư mục
            Files.copy(file.getInputStream(), path.resolve(fileName));

            return fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Ảnh không được để trống");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Dung lượng ảnh vượt quá 2MB");
        }

        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Chỉ chấp nhận ảnh JPEG hoặc PNG");
        }
    }
}
