<script setup></script>

<template>
  <body>
    <div class="container py-4 vw-100">
      <div class="row">
        <div class="col-12 mb-4">
          <a href="/donHang" class="btn btn-outline-brown mb-3">
            <i class="bi bi-arrow-left"></i> Quay lại đơn hàng
          </a>

          <div class="card shadow-sm">
            <div class="card-body">
              <div class="d-flex justify-content-between align-items-center">
                <div>
                  <h4 class="mb-1">Đơn hàng 001</h4>
                  <p class="text-muted mb-0">Đặt vào ngày 27 Tháng 05, 2025 lúc 23:55 tối</p>
                </div>
                <button class="btn btn-outline-brown">
                  <i class="bi bi-printer"></i> In hóa đơn
                </button>
              </div>
            </div>
          </div>
        </div>

     
        <div class="col-lg-8">
          <div class="card shadow-sm mb-4">
            <div class="card-body">
              <h5 class="card-title">Thông tin người nhận</h5>
              <div class="row g-3">
                <div class="col-md-6">
                  <p class="mb-1"><strong>Họ tên:</strong> Vĩ Khiêm</p>
                  <p class="mb-1"><strong>Số điện thoại:</strong> +84 943 671 127</p>
                  <p class="mb-1"><strong>Email:</strong> khiem@gmail.com</p>
                </div>
                <div class="col-md-6">
                  <p class="mb-1"><strong>Địa chỉ giao hàng:</strong></p>
                  <p>394, 2/9<br />Thị Trấn Thứ Ba<br />An Biên<br />Kiên Giang</p>
                </div>
              </div>
            </div>
          </div>

       
          <div class="card shadow-sm mb-4">
            <div class="card-body">
              <h5 class="card-title mb-4">Sản phẩm đã đặt</h5>
              <div class="table-responsive">
                <table class="table">
                  <thead>
                    <tr>
                      <th>Sản phẩm</th>
                      <th>Tên</th>
                      <th>Số lượng</th>
                      <th>Giá</th>
                      <th>Tổng</th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr>
                      <td>
                        <img
                          src="https://images.unsplash.com/photo-1509365465985-25d11c17e812"
                          class="product-img"
                          alt="Product"
                        />
                      </td>
                      <td>Bánh kẹp</td>
                      <td>1</td>
                      <td>60.000 VNĐ</td>
                      <td>60.000 VNĐ</td>
                    </tr>
                    <tr>
                      <td>
                        <img
                          src="https://images.unsplash.com/photo-1578985545062-69928b1d9587"
                          class="product-img"
                          alt="Product"
                        />
                      </td>
                      <td>Bánh sô kem</td>
                      <td>2</td>
                      <td>30.000 VNĐ</td>
                      <td>60.000 VNĐ</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

       
        <div class="col-lg-4">
          <div class="card shadow-sm mb-4">
            <div class="card-body">
              <h5 class="card-title">Trạng thái đơn hàng</h5>
              <div class="progress-tracker">
                <div class="progress" style="height: 5px">
                  <div class="progress-bar bg-success" role="progressbar" style="width: 60%"></div>
                </div>
                <div class="d-flex justify-content-between mt-3">
                  <div class="status-point active">
                    <i class="bi bi-check-circle-fill"></i>
                    <p>Đã đặt</p>
                  </div>
                  <div class="status-point active">
                    <i class="bi bi-check-circle-fill"></i>
                    <p>Đã xác nhận</p>
                  </div>
                  <div class="status-point active">
                    <i class="bi bi-check-circle-fill"></i>
                    <p>Đang xử lý</p>
                  </div>
                  <div class="status-point">
                    <i class="bi bi-circle"></i>
                    <p>Đang giao</p>
                  </div>
                  <div class="status-point">
                    <i class="bi bi-circle"></i>
                    <p>Đã giao</p>
                  </div>
                  <div class="status-point cancelled">
                    <i class="bi bi-circle text-danger"></i>
                    <p>Đã hủy</p>
                  </div>
                </div>
              </div>
            </div>

            
            <hr class="hr-brown" />
            <div class="shadow-sm">
              <div class="card-body">
                <h5 class="card-title">Tóm tắt đơn hàng</h5>
                <div class="d-flex justify-content-between mb-2">
                  <span>Tạm tính</span>
                  <span>120.000 VND</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                  <span>Phí vận chuyển</span>
                  <span>25.000 VND</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                  <span>Giảm giá tổng từng sản phẩm</span>
                  <span class="text-success">-30.000 VND</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                  <span>Giảm giá trên đơn hàng</span>
                  <span class="text-success">-10.000 VND</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                  <span>Giảm phí vận chuyển</span>
                  <span class="text-success">0 VND</span>
                </div>
                <hr class="hr-brown" />
                <div class="d-flex justify-content-between total-amount">
                  <span class="fw-bold">Tổng cộng</span>
                  <span class="fw-bold">80.000 VND</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                  <span>Phương thức thanh toán</span>
                  <span class="text"> Chuyển khoản</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</template>

<style>
:root {
  --primary: #0d6efd;
  --primary-foreground: #ffffff;
  --secondary: #6c757d;
  --secondary-foreground: #ffffff;
  --accent: #e9ecef;
  --accent-foreground: #212529;
  --background: #ffffff;
  --foreground: #212529;
  --card: #ffffff;
  --card-foreground: #212529;
  --border: #dee2e6;
  --input: #dee2e6;
  --ring: #0d6efd;
}

body {
  background-color: var(--accent);
  color: var(--foreground);
}

.card {
  background-color: var(--card);
  border-color: var(--border);
  border-radius: 0.5rem;
}

.product-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 0.25rem;
}

.status-point {
  text-align: center;
  position: relative;
  flex: 1;
}

.status-point i {
  font-size: 1.5rem;
  color: var(--secondary);
}

.status-point.active i {
  color: var(--primary);
}

.status-point p {
  font-size: 0.8rem;
  margin-top: 0.5rem;
  color: var(--secondary);
}

.total-amount {
  font-size: 1.25rem;
  color: var(--primary);
}

.progress-tracker {
  padding: 1rem 0;
}

@media (max-width: 768px) {
  .status-point p {
    font-size: 0.7rem;
  }

  .status-point i {
    font-size: 1.2rem;
  }
}
/* Tùy chỉnh nút outline màu nâu */
.btn-outline-brown {
  color: #c8943f;
 border: 1px solid #c8943f;
  background-color: transparent;
  transition: 0.3s;
}

.btn-outline-brown:hover {
  background-color: #c8943f;
  color: white;
  border-color: #c8943f;
}
.hr-brown {
  border: none;
  border-top: 3px solid #c77e09; /* hoặc 1px nếu muốn mảnh hơn */
  margin: 1rem 0;
}
</style>