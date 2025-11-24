package com.example.demo.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderBean {
	private Integer id;

	private String address;

	private String name;

	private String phone;

	private LocalDateTime orderDate;

	private String paymentStatus;

	private String paymentMethod;

	private BigDecimal totalAmount;

	private String note;

	private BigDecimal shippingFee;

	private BigDecimal discount;

	private String orderStatus;

	private Boolean isAgent;

	private Integer user_id;

	private Integer voucher_id;


}
