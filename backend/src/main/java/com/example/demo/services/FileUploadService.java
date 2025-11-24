package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "images";
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public FileUploadService() {
        // Tạo thư mục images nếu chưa tồn tại
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Could not create upload directory: " + e.getMessage());
        }
    }

    /**
     * Upload một file ảnh và trả về đường dẫn URL
     */
    public String uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // Kiểm tra kích thước file
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File quá lớn. Kích thước tối đa là 5MB");
        }

        // Kiểm tra định dạng file
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidImageFile(originalFilename)) {
            throw new IllegalArgumentException("Định dạng file không được hỗ trợ. Chỉ chấp nhận: " + ALLOWED_EXTENSIONS);
        }

        // Tạo tên file unique
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(fileExtension);

        // Lưu file
        Path targetPath = Paths.get(UPLOAD_DIR, uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Trả về URL path
        return "/" + UPLOAD_DIR + "/" + uniqueFilename;
    }

    /**
     * Upload nhiều file ảnh và trả về danh sách đường dẫn URL
     */
    public List<String> uploadMultipleImages(MultipartFile[] files) throws IOException {
        if (files == null) {
            return new ArrayList<>();
        }

        List<String> uploadedPaths = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                try {
                    String path = uploadImage(file);
                    uploadedPaths.add(path);
                } catch (Exception e) {
                    System.err.println("Failed to upload file " + file.getOriginalFilename() + ": " + e.getMessage());
                    // Có thể chọn throw exception hoặc tiếp tục với các file khác
                    // Ở đây tôi chọn tiếp tục
                }
            }
        }

        return uploadedPaths;
    }

    /**
     * Kiểm tra file có phải là ảnh hợp lệ không
     */
    private boolean isValidImageFile(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    /**
     * Lấy phần mở rộng của file
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * Tạo tên file unique
     */
    private String generateUniqueFilename(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + "." + extension;
    }

    /**
     * Xóa file ảnh
     */
    public boolean deleteImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return false;
            }

            // Loại bỏ dấu "/" đầu nếu có
            String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;

            Path filePath = Paths.get(cleanPath);
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("Deleted image: " + filePath);
                return true;
            } else {
                System.out.println("Image not found: " + filePath);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error deleting image " + imagePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra file ảnh có tồn tại không
     */
    public boolean imageExists(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return false;
            }

            String cleanPath = imagePath.startsWith("/") ? imagePath.substring(1) : imagePath;
            Path filePath = Paths.get(cleanPath);
            return Files.exists(filePath);
        } catch (Exception e) {
            System.err.println("Error checking image existence " + imagePath + ": " + e.getMessage());
            return false;
        }
    }
}