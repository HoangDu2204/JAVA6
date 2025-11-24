package com.example.demo.controllers;

import com.example.demo.dto.AddressRequestDTO;
import com.example.demo.dto.AddressResponseDTO;
import com.example.demo.services.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AddressRestController {

    @Autowired
    private AddressService addressService;

    //  Lấy tất cả địa chỉ của user hiện tại (từ cookie)
    @GetMapping
    public List<AddressResponseDTO> getAllByCurrentUser() {
        return addressService.getList();
    }

    //  Lấy 1 địa chỉ cụ thể (có kiểm tra user sở hữu)
    @GetMapping("/{id}")
    public AddressResponseDTO getById(@PathVariable int id) {
        return addressService.getById(id);
    }

    //  Tạo địa chỉ mới (user lấy từ cookie)
    @PostMapping
    public AddressResponseDTO create(@RequestBody AddressRequestDTO dto) {
        return addressService.create(dto);
    }

    //  Cập nhật địa chỉ (kiểm tra user sở hữu)
    @PutMapping("/{id}")
    public AddressResponseDTO update(@PathVariable int id, @RequestBody AddressRequestDTO dto) {
        return addressService.update(id, dto);
    }

    //  Xoá mềm địa chỉ (kiểm tra user sở hữu)
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable int id) {
        return addressService.delete(id);
    }
}
