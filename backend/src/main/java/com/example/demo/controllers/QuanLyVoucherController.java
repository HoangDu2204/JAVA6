package com.example.demo.controllers;

import com.example.demo.dto.VoucherDTO;
import com.example.demo.entity.Voucher;
import com.example.demo.jpas.VoucherJPA;
import com.example.demo.services.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vouchers")
@CrossOrigin(origins = "http://localhost:5173")
public class QuanLyVoucherController {

    private static final Logger logger = LoggerFactory.getLogger(QuanLyVoucherController.class);

    @Autowired
    private VoucherService voucherService;

    @Autowired
    private VoucherJPA voucherJPA;

    @GetMapping
    public ResponseEntity<?> getAllVouchers() {
        try {
            logger.info("Yêu cầu lấy danh sách tất cả voucher");
            List<Voucher> vouchers = voucherService.getAllVouchers();
            if (vouchers == null || vouchers.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList()); // Trả về danh sách rỗng
            }
            // Convert sang DTO để tránh vấn đề serialization
            List<VoucherDTO> voucherDTOs = vouchers.stream()
                    .map(VoucherDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(voucherDTOs);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy danh sách voucher: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi Server");
            error.put("message", "Không thể lấy danh sách voucher: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVoucherById(@PathVariable Integer id) {
        try {
            logger.info("Yêu cầu lấy voucher với ID: {}", id);
            Voucher voucher = voucherService.getVoucherById(id);
            if (voucher == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không Tìm Thấy");
                error.put("message", "Không tìm thấy voucher với ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            // Convert sang DTO để tránh vấn đề serialization
            VoucherDTO voucherDTO = new VoucherDTO(voucher);
            return ResponseEntity.ok(voucherDTO);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy voucher với ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi Server");
            error.put("message", "Không thể lấy thông tin voucher: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<?> getVoucherByCode(@PathVariable String code) {
        try {
            logger.info("Yêu cầu lấy voucher với mã: {}", code);
            Voucher voucher = voucherJPA.findByCode(code);
            if (voucher == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không Tìm Thấy");
                error.put("message", "Không tìm thấy voucher với mã: " + code);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            // Convert sang DTO để tránh vấn đề serialization
            VoucherDTO voucherDTO = new VoucherDTO(voucher);
            return ResponseEntity.ok(voucherDTO);
        } catch (Exception e) {
            logger.error("Lỗi khi lấy voucher với mã {}: {}", code, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi Server");
            error.put("message", "Không thể lấy thông tin voucher: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVoucher(@PathVariable Integer id) {
        try {
            logger.info("Yêu cầu xóa voucher với ID: {}", id);
            Voucher voucher = voucherService.getVoucherById(id);
            if (voucher == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không Tìm Thấy");
                error.put("message", "Không tìm thấy voucher với ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            voucherService.deleteVoucher(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Lỗi khi xóa voucher với ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi Server");
            error.put("message", "Không thể xóa voucher: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // SỬA LỖI: Sử dụng VoucherDTO thay vì Voucher entity để tránh lỗi Jackson deserialization
    @PostMapping(consumes = {"application/json", "application/json;charset=UTF-8"})
    public ResponseEntity<?> createVoucher(@RequestBody VoucherDTO voucherDTO) {
        try {
            logger.info("Yêu cầu tạo voucher với mã: {}", voucherDTO.getCode());
            if (voucherDTO.getCode() == null || voucherDTO.getCode().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Lỗi Dữ Liệu");
                error.put("message", "Mã voucher không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (voucherJPA.existsByCode(voucherDTO.getCode())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Trùng Lặp");
                error.put("message", "Mã voucher đã tồn tại");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            // Convert DTO sang Entity
            Voucher voucher = voucherDTO.toEntity();
            Voucher savedVoucher = voucherService.saveVoucher(voucher);
            // Convert Entity sang DTO để trả về
            VoucherDTO savedVoucherDTO = new VoucherDTO(savedVoucher);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVoucherDTO);
        } catch (Exception e) {
            logger.error("Lỗi khi tạo voucher: {}", e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi Server");
            error.put("message", "Không thể tạo voucher: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // SỬA LỖI: Sử dụng VoucherDTO thay vì Voucher entity để tránh lỗi Jackson deserialization
    @PutMapping(value = "/{id}", consumes = {"application/json", "application/json;charset=UTF-8"})
    public ResponseEntity<?> updateVoucher(@PathVariable Integer id, @RequestBody VoucherDTO voucherDTO) {
        try {
            logger.info("Yêu cầu cập nhật voucher với ID: {}", id);
            if (voucherDTO.getCode() == null || voucherDTO.getCode().trim().isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Lỗi Dữ Liệu");
                error.put("message", "Mã voucher không được để trống");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            Voucher existingVoucher = voucherService.getVoucherById(id);
            if (existingVoucher == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Không Tìm Thấy");
                error.put("message", "Không tìm thấy voucher với ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            if (!existingVoucher.getCode().equals(voucherDTO.getCode()) && voucherJPA.existsByCode(voucherDTO.getCode())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Trùng Lặp");
                error.put("message", "Mã voucher đã tồn tại");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            // Convert DTO sang Entity và set ID
            Voucher voucher = voucherDTO.toEntity();
            voucher.setId(id);
            Voucher updatedVoucher = voucherService.saveVoucher(voucher);
            // Convert Entity sang DTO để trả về
            VoucherDTO updatedVoucherDTO = new VoucherDTO(updatedVoucher);
            return ResponseEntity.ok(updatedVoucherDTO);
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật voucher với ID {}: {}", id, e.getMessage(), e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi Server");
            error.put("message", "Không thể cập nhật voucher: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


}
