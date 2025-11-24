package com.example.demo.dto;

import lombok.Data;

@Data
public class AddressRequestDTO {
    private String name;
    private String phone;
    private String address;
    private String province;
    private Integer idProvince;
    private String district;
    private Integer idDistrict;
    private String commune;
    private Integer idCommune;
}
