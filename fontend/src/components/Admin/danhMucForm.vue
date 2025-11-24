<script setup>

</script>

<template>
 <div class="d-flex justify-content-center align-items-center min-vh-100 bg-light">
  <div class="card shadow p-2 w-100">
    <div class="container">
      <h2 class="mb-4">Thêm danh mục mới</h2>

      <form>
        <div class="mb-3">
          <label for="categoryName" class="form-label">Tên danh mục</label>
          <input type="text" class="form-control" id="categoryName" placeholder="Nhập tên danh mục">
        </div>

        <div class="mb-3">
          <label for="status" class="form-label">Trạng thái</label>
          <select class="form-select" id="status">
            <option value="active">Đang kinh doanh</option>
            <option value="inactive">Ngừng kinh doanh</option>
          </select>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-gradient-blue">Lưu</button>
          <button type="button" class="btn btn-secondary">Hủy</button>
        </div>
      </form>
    </div>
  </div>
</div>


<!-- <div class="d-flex justify-content-center align-items-center min-vh-100 bg-light">
  <div class="card shadow p-4 w-100">
    <div class="container">
      <h2 class="mb-4">Sửa danh mục</h2>

      <form>
        <div class="mb-3">
          <label for="categoryName" class="form-label">Tên danh mục</label>
          <input type="text" class="form-control" id="categoryName" placeholder="Nhập tên danh mục">
        </div>

        <div class="mb-3">
          <label for="status" class="form-label">Trạng thái</label>
          <select class="form-select" id="status">
            <option value="active">Đang kinh doanh</option>
            <option value="inactive">Ngừng kinh doanh</option>
          </select>
        </div>

        <div class="d-flex gap-2">
          <button type="submit" class="btn btn-gradient-blue">Cập nhật</button>
          <button type="button" class="btn btn-secondary">Hủy</button>
        </div>
      </form>
    </div>
  </div>
</div> -->
</template>

<style>
.btn-gradient-blue {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 6px 14px;
  font-weight: 500;
  transition: color 0.3s ease, box-shadow 0.3s ease;
}

.btn-gradient-blue:hover {
  color: black;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
}

.form-control,
.form-select {
  border-radius: 8px;
  padding: 10px 14px;
  border: 1px solid #ccc;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-control:focus,
.form-select:focus {
  border-color: #3a8ef9;
  box-shadow: 0 0 5px rgba(58, 142, 249, 0.3);
}
.card{
  max-width: 100%;
}

</style>