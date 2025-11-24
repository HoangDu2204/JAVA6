package com.example.demo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String fullName;
    private Integer role;
    private Boolean isActive;


}
