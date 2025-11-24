//package com.example.demo.dto;
//
//import lombok.Data;
//
//@Data
//public class ProductVariantCreateDTONew {
//    private Integer productId;
//    private Integer sizeId;
//    private Integer flavorId;
//    private Integer shapeId;
//    private Integer originId;
//    private Double price;
//    private Integer quantity;
//    private Integer weight;
//    private Boolean isActive;
//}
package com.example.demo.dto;

import lombok.Data;

@Data
public class ProductVariantCreateDTONew {
    // Dữ liệu gửi lên
    private Integer id;
    private Integer productId;
    private Integer sizeId;
    private Integer flavorId;
    private Integer shapeId;
    private Integer originId;
    private Double price;
    private Integer quantity;
    private Integer weight;
    private Boolean isActive;

    // Dữ liệu trả về khi cần hiển thị
    private String sizeName;
    private String flavorName;
    private String shapeName;
    private String originName;
}
