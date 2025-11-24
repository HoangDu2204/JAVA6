package com.example.demo.jpas;

import com.example.demo.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ThongKeJPA extends JpaRepository<Order, Integer> {

    // 1. Doanh thu theo ngày
    @Query("""
        SELECT DATE(o.orderDate), SUM(o.totalAmount), COUNT(o.id)
        FROM Order o
        WHERE o.paymentStatus = 'Đã thanh toán'
        GROUP BY DATE(o.orderDate)
        ORDER BY DATE(o.orderDate) DESC
    """)
    List<Object[]> thongKeDoanhThuTheoNgay();

    // 2. Doanh thu theo tháng
    @Query("""
        SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.totalAmount), COUNT(o.id)
        FROM Order o
        WHERE o.paymentStatus = 'Đã thanh toán'
        GROUP BY YEAR(o.orderDate), MONTH(o.orderDate)
        ORDER BY YEAR(o.orderDate) DESC, MONTH(o.orderDate) DESC
    """)
    List<Object[]> thongKeDoanhThuTheoThang();

    // 3. Doanh thu theo năm
    @Query("""
        SELECT YEAR(o.orderDate), SUM(o.totalAmount), COUNT(o.id)
        FROM Order o
        WHERE o.paymentStatus = 'Đã thanh toán'
        GROUP BY YEAR(o.orderDate)
        ORDER BY YEAR(o.orderDate) DESC
    """)
    List<Object[]> thongKeDoanhThuTheoNam();

    // 4. Đánh giá trung bình theo tháng
    @Query("""
        SELECT p.name, AVG(r.ratings), COUNT(r.id)
        FROM OrderProductRating r
        JOIN r.orderItem oi JOIN oi.productVariant pv JOIN pv.product p
        WHERE MONTH(r.ratingDate) = :thang
        GROUP BY p.id, p.name
        ORDER BY AVG(r.ratings) DESC
    """)
    List<Object[]> thongKeDanhGiaTheoThang(@Param("thang") Integer thang, Pageable pageable);

    // 4. Đánh giá trung bình toàn bộ
    @Query("""
        SELECT p.name, AVG(r.ratings), COUNT(r.id)
        FROM OrderProductRating r
        JOIN r.orderItem oi JOIN oi.productVariant pv JOIN pv.product p
        GROUP BY p.id, p.name
        ORDER BY AVG(r.ratings) DESC
    """)
    List<Object[]> thongKeDanhGia(Pageable pageable);

    // 5. Sản phẩm yêu thích toàn bộ
    @Query(value = """
        SELECT p.name, COUNT(f.id) AS luotYeuThich, c.id AS category_id
        FROM favorites f
        JOIN products p ON f.product_id = p.id
        JOIN categories c ON p.category_id = c.id
        GROUP BY p.id, p.name, c.id
        ORDER BY luotYeuThich DESC
    """, nativeQuery = true)
    List<Object[]> thongKeYeuThich(Pageable pageable);

    // 5. Sản phẩm yêu thích theo tháng
    @Query(value = """
    SELECT p.name, COUNT(f.id) AS luotYeuThich, c.id AS category_id
    FROM favorites f
    JOIN products p ON f.product_id = p.id
    JOIN categories c ON p.category_id = c.id
    WHERE MONTH(f.created_at) = :thang
    GROUP BY p.id, p.name, c.id
    ORDER BY luotYeuThich DESC
""", nativeQuery = true)
    List<Object[]> thongKeYeuThichTheoThang(@Param("thang") int thang, Pageable pageable);

    // 5. Sản phẩm yêu thích theo danh mục
    @Query(value = """
    SELECT p.name, COUNT(f.id) AS luotYeuThich, c.id AS category_id
    FROM favorites f
    JOIN products p ON f.product_id = p.id
    JOIN categories c ON p.category_id = c.id
    WHERE c.id = :id
    GROUP BY p.id, p.name, c.id
    ORDER BY luotYeuThich DESC
""", nativeQuery = true)
    List<Object[]> thongKeYeuThichTheoDanhMuc(@Param("id") int id, Pageable pageable);

    // 5. Sản phẩm yêu thích theo tháng + danh mục
    @Query(value = """
    SELECT p.name, COUNT(f.id) AS luotYeuThich, c.id AS category_id
    FROM favorites f
    JOIN products p ON f.product_id = p.id
    JOIN categories c ON p.category_id = c.id
    WHERE MONTH(f.created_at) = :thang AND c.id = :id
    GROUP BY p.id, p.name, c.id
    ORDER BY luotYeuThich DESC
""", nativeQuery = true)
    List<Object[]> thongKeYeuThichTheoThangVaDanhMuc(@Param("thang") int thang, @Param("id") int id, Pageable pageable);


    // 6. Khách hàng VIP theo tháng
    @Query("""
        SELECT u.id, u.fullName, u.email, u.phone, COUNT(o.id), SUM(o.totalAmount)
        FROM Order o
        JOIN o.user u
        WHERE o.paymentStatus = 'Đã thanh toán' AND MONTH(o.orderDate) = :thang
        GROUP BY u.id, u.fullName, u.email, u.phone
        HAVING COUNT(o.id) >= 1 AND SUM(o.totalAmount) >= 200000
        ORDER BY SUM(o.totalAmount) DESC
    """)
    List<Object[]> thongKeKhachVipTheoThang(@Param("thang") Integer thang, Pageable pageable);

    // 6. Khách hàng VIP toàn bộ
    @Query("""
        SELECT u.id, u.fullName, u.email, u.phone, COUNT(o.id), SUM(o.totalAmount)
        FROM Order o
        JOIN o.user u
        WHERE o.paymentStatus = 'Đã thanh toán'
        GROUP BY u.id, u.fullName, u.email, u.phone
        HAVING COUNT(o.id) >= 1 AND SUM(o.totalAmount) >= 200000
        ORDER BY SUM(o.totalAmount) DESC
    """)
    List<Object[]> thongKeKhachVip(Pageable pageable);
}
