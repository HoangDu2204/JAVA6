////package com.example.demo.controllers;
////
////import com.example.demo.dto.OrderDetailListDTO;
////import com.example.demo.services.OderItemService;
////import com.example.demo.services.OrderListService;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.http.ResponseEntity;
////import org.springframework.web.bind.annotation.*;
////
////@RestController
////@CrossOrigin(origins = "http://localhost:5173")
////@RequestMapping("/api/order-details")
////public class OrderItemRestController {
////
////	@Autowired
////	private OderItemService orderItemService;
////
////	@Autowired
////	private OrderListService orderListService;
////
////	//  Lấy chi tiết đơn hàng theo ID (trả DTO thay vì entity)
////	@GetMapping("/{id}")
////	public ResponseEntity<?> getOrderItem(@PathVariable Integer id) {
////		OrderDetailListDTO dto = orderListService.getOrderDetailById(id);
////		if (dto == null) {
////			return ResponseEntity.notFound().build();
////		}
////		return ResponseEntity.ok(dto);
////	}
////
////	//  Cập nhật trạng thái đơn hàng (giữ nguyên như cũ)
////	@PutMapping("/{id}/orderStatus")
////	public void updateOrderStatus(@PathVariable int id, @RequestParam String orderStatus) {
////		orderItemService.updateStatus(id, orderStatus);
////	}
////}
//package com.example.demo.controllers;
//
//import com.example.demo.dto.OrderDetailListDTO;
//import com.example.demo.dto.OrderListDTO;
//import com.example.demo.entity.User;
//import com.example.demo.jpas.UserJPA;
//import com.example.demo.services.OderItemService;
//import com.example.demo.services.OrderListService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.*;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//
//import java.util.List;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//@RequestMapping("/api/order-details")
//public class OrderItemRestController {
//
//    @Autowired
//    private OderItemService orderItemService;
//
//    @Autowired
//    private OrderListService orderListService;
//
//    @Autowired
//    private UserJPA userJPA;
//
//    //  Lấy chi tiết đơn hàng theo ID
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getOrderItem(@PathVariable Integer id) {
//        OrderDetailListDTO dto = orderListService.getOrderDetailById(id);
//        if (dto == null) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(dto);
//    }
//
//    //  Cập nhật trạng thái đơn hàng
//    @PutMapping("/{id}/orderStatus")
//    public void updateOrderStatus(@PathVariable int id, @RequestParam String orderStatus) {
//        orderItemService.updateStatus(id, orderStatus);
//    }
//
//    //  Lấy danh sách đơn hàng của người dùng từ cookie
//    @GetMapping("/user")
//    public ResponseEntity<?> getOrdersByUser(HttpServletRequest request) {
//        User user = getUserFromCookie(request);
//        if (user == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
//        }
//
//        List<OrderListDTO> orders = orderListService.getOrdersByUserId(user.getId());
//        return ResponseEntity.ok(orders);
//    }
//
//    @GetMapping("/user/{id}")
//    public ResponseEntity<?> getOrderForUser(@PathVariable Integer id, HttpServletRequest request) {
//        User user = getUserFromCookie(request);
//        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//
//        OrderDetailListDTO dto = orderListService.getOrderDetailByIdForUser(id, user.getId());
//        if (dto == null) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền xem đơn hàng này");
//
//        return ResponseEntity.ok(dto);
//    }
//
//    //  Hàm đọc user từ cookie
//    private User getUserFromCookie(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if (cookies == null) return null;
//
//        for (Cookie cookie : cookies) {
//            if ("user_id".equals(cookie.getName())) {
//                try {
//                    int userId = Integer.parseInt(cookie.getValue());
//                    return userJPA.findById(userId).orElse(null);
//                } catch (NumberFormatException e) {
//                    System.out.println("Cookie user_id không hợp lệ");
//                }
//            }
//        }
//        return null;
//    }
//}
package com.example.demo.controllers;

import com.example.demo.dto.OrderDetailListDTO;
import com.example.demo.dto.OrderListDTO;
import com.example.demo.entity.User;
import com.example.demo.jpas.UserJPA;
import com.example.demo.services.OderItemService;
import com.example.demo.services.OrderListService;
import com.example.demo.jwt.AuthHelper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/order-details")
public class OrderItemRestController {

    @Autowired
    private OderItemService orderItemService;

    @Autowired
    private OrderListService orderListService;

    @Autowired
    private AuthHelper authHelper;

    // ✅ Lấy user từ JWT
    private User getUserFromToken(HttpServletRequest request) {
        User user = authHelper.getCurrentUser(request);
        if (user == null) {
            System.out.println("❌ Không lấy được user từ JWT");
        } else {
            System.out.println("✅ User từ JWT: " + user.getUsername());
        }
        return user;
    }

    // 🔹 Lấy chi tiết đơn hàng theo ID (cho Admin)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderItem(@PathVariable Integer id) {
        OrderDetailListDTO dto = orderListService.getOrderDetailById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // 🔹 Cập nhật trạng thái đơn hàng
    @PutMapping("/{id}/orderStatus")
    public void updateOrderStatus(@PathVariable int id, @RequestParam String orderStatus) {
        orderItemService.updateStatus(id, orderStatus);
    }

    // 🔹 Lấy danh sách đơn hàng của người dùng từ JWT
    @GetMapping("/user")
    public ResponseEntity<?> getOrdersByUser(HttpServletRequest request) {
        User user = getUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }

        List<OrderListDTO> orders = orderListService.getOrdersByUserId(user.getId());
        return ResponseEntity.ok(orders);
    }

    // 🔹 Lấy chi tiết đơn hàng cụ thể của user
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getOrderForUser(@PathVariable Integer id, HttpServletRequest request) {
        User user = getUserFromToken(request);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        OrderDetailListDTO dto = orderListService.getOrderDetailByIdForUser(id, user.getId());
        if (dto == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không có quyền xem đơn hàng này");
        }

        return ResponseEntity.ok(dto);
    }


}
