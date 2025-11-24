package com.example.demo.services;

import com.example.demo.dto.OrderDetailListDTO;
import com.example.demo.dto.OrderItemListDTO;
import com.example.demo.dto.OrderListDTO;
import com.example.demo.entity.*;
import com.example.demo.jpas.OrderJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderListService {

    @Autowired
    private OrderJPA orderJPA;

    public Page<OrderListDTO> getAllOrdersForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> ordersPage = orderJPA.findAll(pageable);

        List<OrderListDTO> dtoList = ordersPage.getContent().stream().map(order -> {
            OrderListDTO dto = new OrderListDTO();
            dto.setId(order.getId());
            dto.setName(order.getName());
            dto.setPhone(order.getPhone());
            dto.setAddress(order.getAddress());
            dto.setOrderDate(order.getOrderDate());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setPaymentStatus(order.getPaymentStatus());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setFinalTotal(order.getFinalTotal());

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, ordersPage.getTotalElements());

    }

    //  Lấy danh sách đơn hàng theo UserId
    public List<OrderListDTO> getOrdersByUserId(Integer userId) {
        List<Order> orders = orderJPA.findByUserId(userId);
        List<OrderListDTO> result = new ArrayList<>();

        for (Order order : orders) {
            OrderListDTO dto = new OrderListDTO();
            dto.setId(order.getId());
            dto.setName(order.getName());
            dto.setPhone(order.getPhone());
            dto.setAddress(order.getAddress());
            dto.setOrderDate(order.getOrderDate());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setPaymentStatus(order.getPaymentStatus());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setFinalTotal(order.getFinalTotal());

//            dto.setAgent(order.getAgent() != null);

            result.add(dto);
        }

        return result;
    }


    public OrderDetailListDTO getOrderDetailById(Integer orderId) {
        Order order = orderJPA.findById(orderId).orElse(null);
        if (order == null) return null;

        OrderDetailListDTO dto = new OrderDetailListDTO();
        dto.setId(order.getId());
        dto.setName(order.getName());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setOrderDate(order.getOrderDate());
        dto.setUsername(order.getUser().getUsername());
        dto.setNote(order.getNote());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalTotal(order.getFinalTotal());
        dto.setTotalAmount(order.getTotalAmount());

        dto.setAgent(order.getAgent() != null);


        List<OrderItemListDTO> itemDTOList = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            System.out.println("OrderItem ID from DB: " + item.getId()); // Thêm dòng này
            OrderItemListDTO itemDTO = new OrderItemListDTO();

            itemDTO.setOrderItemId(item.getId());
            System.out.println("DTO after setting: " + itemDTO.getOrderItemId()); // Kiểm tra

            ProductVariant variant = item.getProductVariant();
            Product product = variant.getProduct();

            itemDTO.setProductId(product.getId());
            itemDTO.setProductName(product.getName());

            //  Gán ảnh đại diện sản phẩm (lấy ảnh đầu tiên nếu có)
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                itemDTO.setProductImage(product.getImages().get(0).getUrl());
            } else {
                itemDTO.setProductImage("no-image.jpg"); // fallback nếu không có ảnh
            }

            itemDTO.setSize(variant.getSize().getName());
            itemDTO.setFlavor(variant.getFlavor().getName());
            itemDTO.setShape(variant.getShape().getName());
            itemDTO.setOrigin(variant.getOrigin().getName());

            // Thêm ID để sử dụng cho đánh giá
            itemDTO.setProductId(item.getProductVariant().getProduct().getId());
            itemDTO.setProductVariantId(item.getProductVariant().getId());

//            double originalPrice = variant.getPrice();
//            double discountPercent = 0.0;
//
//            List<ProductDiscount> productDiscounts = product.getProductDiscounts();
//            if (productDiscounts != null && !productDiscounts.isEmpty()) {
//                for (ProductDiscount pd : productDiscounts) {
//                    Discount discount = pd.getDiscount();
//                    if (Boolean.TRUE.equals(discount.getIsActive())
//                            && !discount.getStartDate().isAfter(order.getOrderDate())
//                            && !discount.getEndDate().isBefore(order.getOrderDate())) {
//                        discountPercent = discount.getPercentage();
//                        break;
//                    }
//                }
//            }
//
//            double discountedPrice = originalPrice * (1 - discountPercent / 100.0);
//            itemDTO.setUnitPrice(BigDecimal.valueOf(discountedPrice));
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setQuantity(item.getQuantity());

            itemDTOList.add(itemDTO);
        }

        dto.setItems(itemDTOList);
        return dto;
    }

    //  Cập nhật trạng thái đơn hàng
    public void updateStatus(int id, String orderStatus) {
        Order order = orderJPA.findById(id).orElse(null);
        if (order != null) {
            order.setOrderStatus(orderStatus);
            orderJPA.save(order);
        }
    }


    public OrderDetailListDTO getOrderDetailByIdForUser(Integer orderId, Integer userId) {
        Order order = orderJPA.findById(orderId).orElse(null);
        if (order == null) return null;

        //  Kiểm tra nếu đơn không thuộc về user thì không cho truy cập
        if (!order.getUser().getId().equals(userId)) {
            return null;
        }

        // Bắt đầu map sang DTO
        OrderDetailListDTO dto = new OrderDetailListDTO();
        dto.setId(order.getId());
        dto.setName(order.getName());
        dto.setPhone(order.getPhone());
        dto.setAddress(order.getAddress());
        dto.setOrderDate(order.getOrderDate());
        dto.setUsername(order.getUser().getUsername());
        dto.setNote(order.getNote());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscount(order.getDiscount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setFinalTotal(order.getFinalTotal());
        dto.setTotalAmount(order.getTotalAmount());

        dto.setAgent(order.getAgent() != null);


        List<OrderItemListDTO> itemDTOList = new ArrayList<>();
        for (OrderItem item : order.getOrderItems()) {
            OrderItemListDTO itemDTO = new OrderItemListDTO();

            itemDTO.setOrderItemId(item.getId());//cho đáh giá
            ProductVariant variant = item.getProductVariant();
            Product product = variant.getProduct();

            itemDTO.setProductId(product.getId());
            itemDTO.setProductName(product.getName());

            //  Thêm hình ảnh đầu tiên của sản phẩm
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                itemDTO.setProductImage(product.getImages().get(0).getUrl());
            } else {
                itemDTO.setProductImage("no-image.jpg");
            }

            itemDTO.setSize(variant.getSize().getName());
            itemDTO.setFlavor(variant.getFlavor().getName());
            itemDTO.setShape(variant.getShape().getName());
            itemDTO.setOrigin(variant.getOrigin().getName());

//            double originalPrice = variant.getPrice();
//            double discountPercent = 0.0;
//
//            List<ProductDiscount> productDiscounts = product.getProductDiscounts();
//            if (productDiscounts != null && !productDiscounts.isEmpty()) {
//                for (ProductDiscount pd : productDiscounts) {
//                    Discount discount = pd.getDiscount();
//                    if (Boolean.TRUE.equals(discount.getIsActive())
//                            && !discount.getStartDate().isAfter(order.getOrderDate())
//                            && !discount.getEndDate().isBefore(order.getOrderDate())) {
//                        discountPercent = discount.getPercentage();
//                        break;
//                    }
//                }
//            }
//
//            double discountedPrice = originalPrice * (1 - discountPercent / 100.0);
//            itemDTO.setUnitPrice(BigDecimal.valueOf(discountedPrice));
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setQuantity(item.getQuantity());

            itemDTOList.add(itemDTO);
        }

        dto.setItems(itemDTOList);
        return dto;
    }

    public Page<OrderListDTO> getOrdersForAdminFiltered(int page, int size, String status, String type) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Order> ordersPage;

        if (status != null && !status.isEmpty() && type != null && !type.isEmpty()) {
            if ("user".equalsIgnoreCase(type)) {
                ordersPage = orderJPA.findByOrderStatusAndAgentIsNull(status, pageable);
            } else {
                ordersPage = orderJPA.findByOrderStatusAndAgentIsNotNull(status, pageable);
            }
        } else if (status != null && !status.isEmpty()) {
            ordersPage = orderJPA.findByOrderStatus(status, pageable);
        } else if (type != null && !type.isEmpty()) {
            if ("user".equalsIgnoreCase(type)) {
                ordersPage = orderJPA.findByAgentIsNull(pageable);
            } else {
                ordersPage = orderJPA.findByAgentIsNotNull(pageable);
            }
        } else {
            ordersPage = orderJPA.findAll(pageable);
        }

        List<OrderListDTO> dtoList = ordersPage.getContent().stream().map(order -> {
            OrderListDTO dto = new OrderListDTO();
            dto.setId(order.getId());
            dto.setName(order.getName());
            dto.setPhone(order.getPhone());
            dto.setAddress(order.getAddress());
            dto.setOrderDate(order.getOrderDate());
            dto.setPaymentMethod(order.getPaymentMethod());
            dto.setPaymentStatus(order.getPaymentStatus());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setFinalTotal(order.getFinalTotal());
            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, ordersPage.getTotalElements());
    }


}
