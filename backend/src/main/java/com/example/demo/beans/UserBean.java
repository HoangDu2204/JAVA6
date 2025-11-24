package com.example.demo.beans;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserBean {
	private Integer id;
	@NotBlank(message = "Username không rỗng")
	private String username;

	@NotBlank(message = "Password không rỗng")
	@Length(min = 6, message = "Tối thiểu 6 ký tự")
	private String password;

	@NotBlank(message = "Email không rỗng")
	@Email(message = "Email sai định dạng")
	private String email;

	@NotBlank(message = "Name không rỗng")
	private String name;
	@NotBlank(message = "Address không rỗng")
	private String address;

}
