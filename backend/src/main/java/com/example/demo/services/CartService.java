
package com.example.demo.services;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.*;
import com.example.demo.jpas.CartItemJPA;
import com.example.demo.jpas.CartJPA;
import com.example.demo.jpas.ProductVariantJPA;
import com.example.demo.jwt.AuthHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CartService {

  @Autowired
  private HttpServletRequest request;

  @Autowired
  private AuthHelper authHelper;

  @Autowired
  private CartJPA cartJPA;

  @Autowired
  private CartItemJPA cartItemJPA;

  @Autowired
  private ProductVariantJPA productVariantJPA;

  // ✅ Lấy user từ JWT thay vì cookie
  public User getUser() {
    User user = authHelper.getCurrentUser(request);
    if (user == null) {
      System.out.println("❌ Không lấy được user từ token!");
    } else {
      System.out.println("✅ Đã lấy user từ token: " + user.getUsername());
    }
    return user;
  }

  // Lấy giỏ hàng hoặc tạo mới nếu chưa có
  public Cart getCart() {
    User user = getUser();
    if (user == null) return null;

    List<Cart> carts = user.getCarts();
    if (carts != null && !carts.isEmpty()) {
      return carts.get(0);
    }

    Cart cart = new Cart();
    cart.setUser(user);
    return cartJPA.save(cart);
  }

  // Lấy danh sách cart item
  public List<CartItem> getList() {
    Cart cart = getCart();
    return cart != null ? cart.getCartItems() : new ArrayList<>();
  }

  public boolean addToCartByVariant(int variantId, int quantity) {
    Cart cart = getCart();
    if (cart == null) return false;

    ProductVariant variant = productVariantJPA.findById(variantId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm."));

    Optional<CartItem> cartItemOptional = cartItemJPA.findByVariantIdAndCartId(variantId, cart.getId());

    int currentQuantityInCart = cartItemOptional.map(CartItem::getQuantity).orElse(0);
    int totalQuantity = currentQuantityInCart + quantity;

    if (totalQuantity > variant.getQuantity()) {
      throw new RuntimeException("Số lượng vượt quá tồn kho! Chỉ còn " + variant.getQuantity() + " sản phẩm.");
    }

    if (cartItemOptional.isPresent()) {
      CartItem existingItem = cartItemOptional.get();
      existingItem.setQuantity(totalQuantity);
      cartItemJPA.save(existingItem);
    } else {
      CartItem newItem = new CartItem();
      newItem.setCart(cart);
      newItem.setProductVariant(variant);
      newItem.setQuantity(quantity);
      cartItemJPA.save(newItem);
    }

    return true;
  }

  // Xoá sản phẩm trong giỏ
  public boolean deleteCartItem(int cartItemId) {
    try {
      Cart cart = getCart();
      Optional<CartItem> optional = cartItemJPA.findByIdAndCartId(cartItemId, cart.getId());
      if (optional.isEmpty()) return false;
      cartItemJPA.deleteById(cartItemId);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean updateQuantityCartItem(int cartItemId, int quantity) {
    Cart cart = getCart();
    CartItem item = cartItemJPA.findByIdAndCartId(cartItemId, cart.getId())
            .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại trong giỏ hàng."));

    ProductVariant variant = item.getProductVariant();

    if (quantity > variant.getQuantity()) {
      throw new RuntimeException("Số lượng vượt quá tồn kho! Chỉ còn " + variant.getQuantity() + " sản phẩm.");
    }

    if (quantity <= 0) {
      return deleteCartItem(cartItemId);
    }

    item.setQuantity(quantity);
    cartItemJPA.save(item);
    return true;
  }




  // Tính tổng tiền sau giảm
  public double calculateTotalPrice(List<CartItem> items) {
    double total = 0;
    for (CartItem item : items) {
      ProductVariant variant = item.getProductVariant();
      Product product = variant.getProduct();

      double price = variant.getPrice() != null ? variant.getPrice() : 0.0;
      double discount = 0.0;

      if (product != null && product.getProductDiscounts() != null) {
        for (ProductDiscount pd : product.getProductDiscounts()) {
          Discount d = pd.getDiscount();
          if (d != null && Boolean.TRUE.equals(d.getIsActive())
                  && d.getStartDate() != null && d.getEndDate() != null
                  && !d.getStartDate().isAfter(LocalDateTime.now())
                  && !d.getEndDate().isBefore(LocalDateTime.now())) {
            discount = d.getPercentage();
            break;
          }
        }
      }

      double discountedPrice = price * (1 - discount / 100);
      total += discountedPrice * item.getQuantity();
    }
    return total;
  }

  // Trả về danh sách CartItemDTO
  public List<CartItemDTO> getCartItemDTOs() {
    Cart cart = getCart();
    if (cart == null || cart.getCartItems() == null) return new ArrayList<>();

    List<CartItemDTO> dtos = new ArrayList<>();

    for (CartItem item : cart.getCartItems()) {
      CartItemDTO dto = new CartItemDTO();
      dto.setId(item.getId());
      dto.setQuantity(item.getQuantity());

      ProductVariant variant = item.getProductVariant();
      if (variant != null) {
        Product product = variant.getProduct();

        double price = variant.getPrice() != null ? variant.getPrice() : 0.0;
        dto.setProductPrice(price);

        double discount = 0.0;
        if (product != null && product.getProductDiscounts() != null) {
          for (ProductDiscount pd : product.getProductDiscounts()) {
            Discount d = pd.getDiscount();
            if (d != null && Boolean.TRUE.equals(d.getIsActive())
                    && d.getStartDate() != null && d.getEndDate() != null
                    && !d.getStartDate().isAfter(LocalDateTime.now())
                    && !d.getEndDate().isBefore(LocalDateTime.now())) {
              discount = d.getPercentage();
              break;
            }
          }
        }
        dto.setDiscountPercentage(discount);

        double discountedPrice = price * (1 - discount / 100);
        dto.setDiscountedPrice(discountedPrice);

        List<String> variantLines = new ArrayList<>();
        if (variant.getSize() != null) variantLines.add("Size: " + variant.getSize().getName());
        if (variant.getFlavor() != null) variantLines.add("Vị: " + variant.getFlavor().getName());
        if (variant.getShape() != null) variantLines.add("Hình: " + variant.getShape().getName());
        if (variant.getOrigin() != null) variantLines.add("Xuất xứ: " + variant.getOrigin().getName());
        dto.setVariantLines(variantLines);

        dto.setVariantId(variant.getId());

        if (product != null) {
          dto.setProductName(product.getName());
          if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setProductImage(product.getImages().get(0).getUrl());
          }
        }
      }

      dtos.add(dto);
    }

    return dtos;
  }
}



