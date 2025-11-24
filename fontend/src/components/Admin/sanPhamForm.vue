<script setup>

</script>

<template>
 <div class="container">
    <div class="card shadow p-4 mx-auto" >
      <h2 class="mb-4 text-center">Thêm sản phẩm mới</h2>

      <form>
        <div class="mb-3">
          <label for="productName" class="form-label">Tên sản phẩm</label>
          <input type="text" class="form-control" id="productName" placeholder="Nhập tên sản phẩm">
        </div>

        <div class="mb-3">
          <label for="productDescription" class="form-label">Mô tả</label>
          <textarea class="form-control" id="productDescription" rows="3" placeholder="Nhập mô tả sản phẩm"></textarea>
        </div>

        <div class="mb-3">
          <label for="productImage" class="form-label">Hình ảnh</label>
          <input type="file" class="form-control" id="productImage" accept="image/*">
        </div>

        <div class="mb-3">
          <label for="createDate" class="form-label">Ngày tạo</label>
          <input type="date" class="form-control" id="createDate">
        </div>

        <div class="mb-3">
          <label for="status" class="form-label">Trạng thái</label>
          <select class="form-select" id="status">
            <option value="active">Đang bán</option>
            <option value="inactive">Tạm ngừng</option>
          </select>
        </div>

        <div class="d-flex gap-2 justify-content-end mt-4">
          <button type="submit" class="btn btn-gradient-blue">Thêm sản phẩm</button>
          <button type="button" class="btn btn-secondary">Hủy</button>
        </div>
      </form>
    </div>
  </div>

  <!-- <div class="container py-5">
    <div class="card shadow p-4 mx-auto" >
      <h2 class="mb-4 text-center">Sửa sản phẩm</h2>

      <form>
        <div class="mb-3">
          <label for="productName" class="form-label">Tên sản phẩm</label>
          <input type="text" class="form-control" id="productName" placeholder="Nhập tên sản phẩm">
        </div>

        <div class="mb-3">
          <label for="productDescription" class="form-label">Mô tả</label>
          <textarea class="form-control" id="productDescription" rows="3" placeholder="Nhập mô tả sản phẩm"></textarea>
        </div>

        <div class="mb-3">
          <label for="productImage" class="form-label">Hình ảnh</label>
          <input type="file" class="form-control" id="productImage" accept="image/*">
        </div>

        <div class="mb-3">
          <label for="createDate" class="form-label">Ngày tạo</label>
          <input type="date" class="form-control" id="createDate">
        </div>

        <div class="mb-3">
          <label for="status" class="form-label">Trạng thái</label>
          <select class="form-select" id="status">
            <option value="active">Đang bán</option>
            <option value="inactive">Tạm ngừng</option>
          </select>
        </div>

        <div class="d-flex gap-2 justify-content-end mt-4">
          <button type="submit" class="btn btn-gradient-blue">Sửa sản phẩm</button>
          <button type="button" class="btn btn-secondary">Hủy</button>
        </div>
      </form>
    </div>
  </div> -->
</template>

<style scoped>
.card {
  border-radius: 16px;
}

.btn-gradient-blue {
  background-image: linear-gradient(-135deg, #5ee7df 0%, #3a8ef9 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 20px;
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

h2 {
  font-size: 1.75rem;
  font-weight: 600;
  color: #3a3a3a;
}
.card{
  max-width: 60%;
}
</style>