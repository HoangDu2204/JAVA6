//
//package com.example.demo.controllers;
//
//import com.example.demo.dto.CartItemDTO;
//import com.example.demo.entity.CartItem;
//import com.example.demo.services.CartService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//@RequestMapping("/api/cart")
//@RequiredArgsConstructor
//public class CartRestController {
//
//    private final CartService cartService;
//
//    // GET /api/cart
//    @GetMapping
//    public ResponseEntity<?> getCartItems() {
//        List<CartItemDTO> items = cartService.getCartItemDTOs();
//        if (items == null) return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
//        return ResponseEntity.ok(items);
//    }
//
//    // GET /api/cart/total
//    @GetMapping("/total")
//    public ResponseEntity<?> getTotal() {
//        List<CartItem> items = cartService.getList();
//        if (items == null) return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
//        return ResponseEntity.ok(cartService.calculateTotalPrice(items));
//    }
//
//    // POST /api/cart/add  (Body: { "variantId": 1, "quantity": 2 })
//    @PostMapping("/add")
//    public ResponseEntity<?> addToCart(@RequestBody Map<String, Integer> body) {
//        Integer variantId = body.get("variantId");
//        Integer quantity = body.getOrDefault("quantity", 1);
//        boolean success = cartService.addToCartByVariant(variantId, quantity);
//        if (!success) return ResponseEntity.status(401).body("Thêm vào giỏ thất bại. Có thể bạn chưa đăng nhập.");
//        return ResponseEntity.ok("Đã thêm vào giỏ hàng");
//    }
//
//    // PUT /api/cart/update  (Body: { "cartItemId": 1, "quantity": 5 })
//    @PutMapping("/update")
//    public ResponseEntity<?> updateCartItem(@RequestBody Map<String, Integer> body) {
//        Integer cartItemId = body.get("cartItemId");
//        Integer quantity = body.get("quantity");
//        boolean updated = cartService.updateQuantityCartItem(cartItemId, quantity);
//        if (!updated) return ResponseEntity.badRequest().body("Không thể cập nhật sản phẩm trong giỏ hàng");
//        return ResponseEntity.ok("Đã cập nhật giỏ hàng");
//    }
//
//    // DELETE /api/cart/delete  (Body: { "cartItemId": 123 })
//    @DeleteMapping("/delete")
//    public ResponseEntity<?> deleteCartItem(@RequestBody Map<String, Integer> body) {
//        Integer cartItemId = body.get("cartItemId");
//        boolean deleted = cartService.deleteCartItem(cartItemId);
//        if (!deleted) return ResponseEntity.badRequest().body("Không thể xóa sản phẩm khỏi giỏ hàng");
//        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
//    }
//}
//
package com.example.demo.controllers;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.CartItem;
import com.example.demo.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequestMapping("/api/cart")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<?> getCartItems() {
        List<CartItemDTO> items = cartService.getCartItemDTOs();
        if (items == null) return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        return ResponseEntity.ok(items);
    }

    @GetMapping("/total")
    public ResponseEntity<?> getTotal() {
        List<CartItem> items = cartService.getList();
        if (items == null) return ResponseEntity.status(401).body("Bạn chưa đăng nhập");
        return ResponseEntity.ok(cartService.calculateTotalPrice(items));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Integer> body) {
        try {
            Integer variantId = body.get("variantId");
            Integer quantity = body.getOrDefault("quantity", 1);
            boolean success = cartService.addToCartByVariant(variantId, quantity);
            if (!success)
                return ResponseEntity.status(401).body(Map.of(
                        "error", "Thêm vào giỏ thất bại. Có thể bạn chưa đăng nhập."
                ));
            return ResponseEntity.ok("Đã thêm vào giỏ hàng");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    @PutMapping("/update")
    public ResponseEntity<?> updateCartItem(@RequestBody Map<String, Integer> body) {
        try {
            Integer cartItemId = body.get("cartItemId");
            Integer quantity = body.get("quantity");
            boolean updated = cartService.updateQuantityCartItem(cartItemId, quantity);
            if (!updated) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Không thể cập nhật sản phẩm trong giỏ hàng"
                ));
            }
            return ResponseEntity.ok("Đã cập nhật giỏ hàng");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCartItem(@RequestBody Map<String, Integer> body) {
        Integer cartItemId = body.get("cartItemId");
        boolean deleted = cartService.deleteCartItem(cartItemId);
        if (!deleted) return ResponseEntity.badRequest().body("Không thể xóa sản phẩm khỏi giỏ hàng");
        return ResponseEntity.ok("Đã xóa sản phẩm khỏi giỏ hàng");
    }
}
