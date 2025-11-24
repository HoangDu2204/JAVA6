package com.example.demo.config;//package com.example.demo.config;


import com.example.demo.component.PermissionComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PermissionConfig implements WebMvcConfigurer{

  @Autowired
  PermissionComponent permissionComponents;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(permissionComponents)
    .addPathPatterns("/admin/*", "/user/*"); // Nhận những đường dẫn được đi qua interceptor
    // .excludePathPatterns("/login", "/", "/contact", "/about-us"); // Loại bỏ những đường dẫn được đi qua interceptor
  }


}