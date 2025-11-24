<template>
  <div class="container ">
    <div class="page-header mb-4">
     
      <div class="header-breadcrumb-row">
        <h5 class="page-title mb-1">Thêm chiết khấu </h5>
        <nav aria-label="breadcrumb">
          <ol class="breadcrumb bg-transparent p-0 m-0">
            <li class="breadcrumb-item"><a href="#">Home</a></li>
            <li class="breadcrumb-item"><a href="/QuanLyChietKhau">Quản lý chiết khấu</a></li>
            <li class="breadcrumb-item active" aria-current="page">Thêm chiết khấu</li>
          </ol>
        </nav>
      </div>
    </div>

    <div class="card">
      <div class="card-body p-4">
        <form action="/A/QuanLyDaiLy" method="post" enctype="multipart/form-data" class="add-discount-form">
          
          <div class="mb-3">
            <label class="form-label">Chiết khấu (%)</label>
            <input type="number" name="discount_percentage" class="form-control" required placeholder="Nhập phần trăm chiết khấu" min="0" max="100" />
          </div>
          <div class="mb-3">
            <label class="form-label">Ngày bắt đầu</label>
            <input type="date" name="start_date" class="form-control" required />
          </div>
          <div class="mb-3">
            <label class="form-label">Ngày kết thúc</label>
            <input type="date" name="end_date" class="form-control" required />
          </div>
          <div class="d-flex justify-content-end">
            <button type="submit" class="btn btn-gradient-approve me-2">
              <i class="bi bi-check-lg me-1"></i> Lưu
            </button>
            <a href="/A/QuanLyDaiLy" class="btn btn-gradient-reject">
              <i class="bi bi-x-lg me-1"></i> Hủy
            </a>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  padding: 1rem 1.5rem;
}

.btn-icon {
  background: none;
  border: none;
  color: #495057;
  font-size: 1.1rem;
  padding: 0.25rem 0.5rem;
}

.btn-icon:hover {
  color: #212529;
}

.header-right-icons .badge {
  font-size: 0.65em;
  padding: 0.25em 0.4em;
  background-color: #17a2b8 !important;
}

.page-title {
  font-size: 1.4rem;
  font-weight: 600;
  color: #343a40;
}

.breadcrumb {
  font-size: 0.85rem;
}

.breadcrumb-item a {
  color: #007bff;
  text-decoration: none;
}

.breadcrumb-item a:hover {
  text-decoration: underline;
}

.breadcrumb-item.active {
  color: #6c757d;
}

.container {
  background-color: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
}

.card-header {
  padding: 1rem 1.25rem;
  min-height: 48px;
}

.card-header h5 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #343a40;
}

.card-body {
  padding: 1.5rem;
}

.btn {
  border: none;
  color: rgb(61, 58, 58);
  border-radius: 1rem;
  padding: 0.35rem 0.8rem;
  font-weight: 500;
  transition: opacity 0.2s ease;
  cursor: pointer;
}

.btn:hover {
  opacity: 0.9;
  color: rgb(61, 58, 58);
}

.btn-gradient-approve {
  background-image: linear-gradient(to right, #efc69a, #f3b275);
}

.btn-gradient-reject {
  background-image: linear-gradient(to right, #fa8b8b, #ef5951);
}

.btn-sm {
  font-size: 0.8rem;
  display: inline-flex;
  align-items: center;
}

.text-muted {
  color: #6c757d !important;
}

.ms-2 {
  margin-left: 0.5rem !important;
}

.me-1 {
  margin-right: 0.25rem !important;
}

.me-2 {
  margin-right: 0.5rem !important;
}

.p-4 {
  padding: 1.5rem !important;
}

.btn i {
  vertical-align: middle;
  line-height: 1;
}

.d-flex { display: flex !important; }
.justify-content-between { justify-content: space-between !important; }
.align-items-center { align-items: center !important; }
.mb-4 { margin-bottom: 1.5rem !important; }
.mb-3 { margin-bottom: 1rem !important; }
.mb-1 { margin-bottom: 0.25rem !important; }
.p-0 { padding: 0 !important; }
.m-0 { margin: 0 !important; }
.bg-transparent { background-color: transparent !important; }
.position-relative { position: relative !important; }
.position-absolute { position: absolute !important; }
.top-0 { top: 0 !important; }
.start-100 { left: 100% !important; }
.translate-middle { transform: translate(-50%, -50%) !important; }
.badge { display: inline-block; padding: .35em .65em; font-size: .75em; font-weight: 700; line-height: 1; color: #fff; text-align: center; white-space: nowrap; vertical-align: baseline; border-radius: .25rem; }
.rounded-pill { border-radius: 50rem !important; }
.bg-info { background-color: #17a2b8 !important; }
.visually-hidden { position: absolute !important; width: 1px !important; height: 1px !important; padding: 0 !important; margin: -1px !important; overflow: hidden !important; clip: rect(0, 0, 0, 0) !important; white-space: nowrap !important; border: 0 !important; }

.add-discount-form {
  background-color: #ffffff;
}

.add-discount-form .form-label {
  font-weight: 500;
  color: #495057;
}

.add-discount-form .form-control {
  border: 1px solid #ced4da;
  border-radius: 0.25rem;
}

.add-discount-form .form-control:focus {
  box-shadow: none;
  border-color: #007bff;
}

.settings-dropdown-wrapper:hover .settings-dropdown {
  visibility: visible;
  opacity: 1;
  pointer-events: auto;
}
</style>