//package com.example.demo.services;
//
//import com.example.demo.dto.AddressRequestDTO;
//import com.example.demo.dto.AddressResponseDTO;
//import com.example.demo.entity.Address;
//import com.example.demo.entity.User;
//import com.example.demo.jpas.AddressJPA;
//import com.example.demo.jpas.UserJPA;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class AddressService {
//
//    @Autowired
//    private HttpServletRequest request;
//
//    @Autowired
//    private AddressJPA addressRepository;
//
//    @Autowired
//    private UserJPA userRepository;
//
//    // ✅ Lấy user từ cookie
//    public User getUser() {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) {
//            System.out.println("Không có cookie nào được gửi lên!");
//            return null;
//        }
//
//        for (Cookie cookie : cookies) {
//            System.out.println("Cookie nhận được: " + cookie.getName() + "=" + cookie.getValue());
//            if ("user_id".equals(cookie.getName())) {
//                try {
//                    return userRepository.findById(Integer.parseInt(cookie.getValue())).orElse(null);
//                } catch (NumberFormatException e) {
//                    System.out.println("Lỗi chuyển đổi user_id từ cookie");
//                    return null;
//                }
//            }
//        }
//
//        System.out.println("Không tìm thấy cookie tên 'user_id'");
//        return null;
//    }
//
//    //  Lấy danh sách địa chỉ của người dùng hiện tại
//    public List<AddressResponseDTO> getList() {
//        User user = getUser();
//        if (user == null) return new ArrayList<>();
//
//        List<Address> list = addressRepository.findByUserAndIsActiveTrue(user);
//        List<AddressResponseDTO> dtos = new ArrayList<>();
//        for (Address address : list) {
//            dtos.add(convertToDTO(address));
//        }
//        return dtos;
//    }
//
//    //  Lấy 1 địa chỉ theo ID
//    public AddressResponseDTO getById(int id) {
//        Address address = addressRepository.findById(id).orElse(null);
//        if (address == null || !address.getIsActive()) return null;
//
//        // Kiểm tra xem địa chỉ có thuộc về user hiện tại không
//        User user = getUser();
//        if (user == null || !address.getUser().getId().equals(user.getId())) return null;
//
//        return convertToDTO(address);
//    }
//
//    //  Tạo địa chỉ mới
//    public AddressResponseDTO create(AddressRequestDTO req) {
//        User user = getUser();
//        if (user == null) throw new RuntimeException("Không tìm thấy người dùng từ cookie");
//
//        Address address = new Address();
//        address.setUser(user);
//        address.setName(req.getName());
//        address.setPhone(req.getPhone());
//        address.setAddress(req.getAddress());
//        address.setProvince(req.getProvince());
//        address.setIdProvince(req.getIdProvince());
//        address.setDistrict(req.getDistrict());
//        address.setIdDistrict(req.getIdDistrict());
//        address.setCommune(req.getCommune());
//        address.setIdCommune(req.getIdCommune());
//        address.setIsActive(true);
//
//        Address saved = addressRepository.save(address);
//        return convertToDTO(saved);
//    }
//
//    //  Cập nhật địa chỉ (chỉ nếu thuộc user hiện tại)
//    public AddressResponseDTO update(int id, AddressRequestDTO req) {
//        User user = getUser();
//        if (user == null) return null;
//
//        Address address = addressRepository.findById(id).orElse(null);
//        if (address == null || !address.getIsActive()) return null;
//
//        if (!address.getUser().getId().equals(user.getId())) return null;
//
//        address.setName(req.getName());
//        address.setPhone(req.getPhone());
//        address.setAddress(req.getAddress());
//        address.setProvince(req.getProvince());
//        address.setIdProvince(req.getIdProvince());
//        address.setDistrict(req.getDistrict());
//        address.setIdDistrict(req.getIdDistrict());
//        address.setCommune(req.getCommune());
//        address.setIdCommune(req.getIdCommune());
//
//        Address saved = addressRepository.save(address);
//        return convertToDTO(saved);
//    }
//
//    //  Xoá mềm địa chỉ (chỉ nếu thuộc user hiện tại)
//    public boolean delete(int id) {
//        User user = getUser();
//        if (user == null) return false;
//
//        Address address = addressRepository.findById(id).orElse(null);
//        if (address == null || !address.getIsActive()) return false;
//
//        if (!address.getUser().getId().equals(user.getId())) return false;
//
//        address.setIsActive(false);
//        addressRepository.save(address);
//        return true;
//    }
//
//    private AddressResponseDTO convertToDTO(Address a) {
//        AddressResponseDTO dto = new AddressResponseDTO();
//        dto.setId(a.getId());
//        dto.setName(a.getName());
//        dto.setPhone(a.getPhone());
//        dto.setAddress(a.getAddress());
//        dto.setProvince(a.getProvince());
//        dto.setIdProvince(a.getIdProvince());
//        dto.setDistrict(a.getDistrict());
//        dto.setIdDistrict(a.getIdDistrict());
//        dto.setCommune(a.getCommune());
//        dto.setIdCommune(a.getIdCommune());
//        return dto;
//    }
//}
package com.example.demo.services;

import com.example.demo.dto.AddressRequestDTO;
import com.example.demo.dto.AddressResponseDTO;
import com.example.demo.entity.Address;
import com.example.demo.entity.User;
import com.example.demo.jpas.AddressJPA;
import com.example.demo.jwt.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final HttpServletRequest request;
    private final AddressJPA addressRepository;
    private final AuthHelper authHelper;

    //  Lấy user từ JWT token
    public User getUser() {
        return authHelper.getCurrentUser(request);
    }

    //  Lấy danh sách địa chỉ của người dùng hiện tại
    public List<AddressResponseDTO> getList() {
        User user = getUser();
        if (user == null) return new ArrayList<>();

        List<Address> list = addressRepository.findByUserAndIsActiveTrue(user);
        List<AddressResponseDTO> dtos = new ArrayList<>();
        for (Address address : list) {
            dtos.add(convertToDTO(address));
        }
        return dtos;
    }

    //  Lấy 1 địa chỉ theo ID (nếu thuộc user)
    public AddressResponseDTO getById(int id) {
        Address address = addressRepository.findById(id).orElse(null);
        if (address == null || !address.getIsActive()) return null;

        User user = getUser();
        if (user == null || !address.getUser().getId().equals(user.getId())) return null;

        return convertToDTO(address);
    }

    //  Tạo địa chỉ mới
    public AddressResponseDTO create(AddressRequestDTO req) {
        User user = getUser();
        if (user == null) throw new RuntimeException("Không tìm thấy người dùng từ token");

        Address address = new Address();
        address.setUser(user);
        address.setName(req.getName());
        address.setPhone(req.getPhone());
        address.setAddress(req.getAddress());
        address.setProvince(req.getProvince());
        address.setIdProvince(req.getIdProvince());
        address.setDistrict(req.getDistrict());
        address.setIdDistrict(req.getIdDistrict());
        address.setCommune(req.getCommune());
        address.setIdCommune(req.getIdCommune());
        address.setIsActive(true);

        Address saved = addressRepository.save(address);
        return convertToDTO(saved);
    }

    //  Cập nhật địa chỉ (nếu thuộc user)
    public AddressResponseDTO update(int id, AddressRequestDTO req) {
        User user = getUser();
        if (user == null) return null;

        Address address = addressRepository.findById(id).orElse(null);
        if (address == null || !address.getIsActive()) return null;
        if (!address.getUser().getId().equals(user.getId())) return null;

        address.setName(req.getName());
        address.setPhone(req.getPhone());
        address.setAddress(req.getAddress());
        address.setProvince(req.getProvince());
        address.setIdProvince(req.getIdProvince());
        address.setDistrict(req.getDistrict());
        address.setIdDistrict(req.getIdDistrict());
        address.setCommune(req.getCommune());
        address.setIdCommune(req.getIdCommune());

        Address saved = addressRepository.save(address);
        return convertToDTO(saved);
    }

    //  Xoá mềm địa chỉ (nếu thuộc user)
    public boolean delete(int id) {
        User user = getUser();
        if (user == null) return false;

        Address address = addressRepository.findById(id).orElse(null);
        if (address == null || !address.getIsActive()) return false;
        if (!address.getUser().getId().equals(user.getId())) return false;

        address.setIsActive(false);
        addressRepository.save(address);
        return true;
    }

    //  Chuyển Address entity → DTO
    private AddressResponseDTO convertToDTO(Address a) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setId(a.getId());
        dto.setName(a.getName());
        dto.setPhone(a.getPhone());
        dto.setAddress(a.getAddress());
        dto.setProvince(a.getProvince());
        dto.setIdProvince(a.getIdProvince());
        dto.setDistrict(a.getDistrict());
        dto.setIdDistrict(a.getIdDistrict());
        dto.setCommune(a.getCommune());
        dto.setIdCommune(a.getIdCommune());

        if (a.getUser() != null) {
            dto.setEmail(a.getUser().getEmail());
        }
        return dto;
    }
}
