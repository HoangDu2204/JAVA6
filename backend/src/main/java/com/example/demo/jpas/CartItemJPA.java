package com.example.demo.jpas;

import com.example.demo.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CartItemJPA extends JpaRepository<CartItem, Integer> {

	// Tìm cart item theo variant & cart
	@Query(value = "SELECT * FROM cart_items WHERE product_variant_id = ?1 AND cart_id = ?2", nativeQuery = true)
	Optional<CartItem> findByVariantIdAndCartId(Integer variantId, Integer cartId);

	// Tìm cart item theo id & cart để kiểm tra quyền/xoá
	@Query(value = "SELECT * FROM cart_items WHERE id = ?1 AND cart_id = ?2", nativeQuery = true)
	Optional<CartItem> findByIdAndCartId(Integer cartItemId, Integer cartId);
}
