<template>
  <div class="lien-he">
    <main>
      <div>
        <section
          class="bread-crumb"
          style="
            background:
              linear-gradient(0deg, rgba(0, 0, 0, 0.8), rgba(0, 0, 0, 0.3)),
              url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436)
                center no-repeat;
          "
        >
          <div class="container">
            <div class="title-bread-crumb" style="">Tin tức</div>
            <ul class="breadcrumb">
              <li class="home">
                <a href="/"><span>Trang chủ ></span></a>
                <a href="/"><span>Tin tức</span></a>
              </li>
            </ul>
          </div>
        </section>

        <div class="abc">
          <!-- Nội dung chính -->
          <main class="abc">
            <section class="articles">
              <article v-for="(tin, index) in paginatedNews" :key="tin.id">
                <div class="image-container">
                  <input
                    type="checkbox"
                    class="zoom-checkbox"
                    :id="'zoom' + index"
                    style="display: none"
                  />
                  <label :for="'zoom' + index">
                    <img :src="tin.hinhAnh" :alt="tin.tieuDe" />
                  </label>
                </div>
                <h3>
                  <RouterLink :to="`/binhLuanTinTuc/${tin.id}`">{{ tin.tieuDe }}</RouterLink>
                </h3>
                <p v-html="formatNoiDung(tin.noiDung)"></p>
                <span class="date">{{ tin.ngayDang }}</span>
              </article>
            </section>
          </main>

          <aside class="right-sidebar">
            <section class="sidebar">
              <h2>Danh mục tin tức</h2>
              <ul>
                <li><a href="/">Trang chủ</a></li>
                <li><a href="/gioiThieu">Giới thiệu</a></li>
                <li><a href="/sanPham">Sản phẩm</a></li>
                <li><a href="/tinTuc">Tin tức</a></li>
                <li><a href="/lienHe">Liên hệ</a></li>
                <li><a href="/heThongCuaHang">Hệ thống cửa hàng</a></li>
                <li><a href="/cauHoiThuongGap">Câu hỏi thường gặp</a></li>
              </ul>
            </section>

            <section class="sidebar">
              <h2>Tin tức nổi bật</h2>
              <ul class="highlight-news">
                <li v-for="(tin, index) in danhSachTinTuc.slice(0, 5)" :key="tin.id">
                  <a :href="`/binhLuanTinTuc/${tin.id}`">
                    <div class="number">{{ index + 1 }}</div>
                    <img :src="tin.hinhAnh" :alt="tin.tieuDe" />
                    <div class="content">
                      <h3>{{ tin.tieuDe }}</h3>
                      <span class="date">{{ tin.ngayDang }}</span>
                    </div>
                  </a>
                </li>
              </ul>
            </section>
            </aside>
        </div>

      <!-- 📄 Phân trang -->
<div v-if="totalPages > 1" class="pagination-wrapper">
  <ul class="pagination">
    <li
      class="page-item"
      :class="{ disabled: currentPage === 1 }"
      @click="goToPage(currentPage - 1)"
    >
      <a class="page-link" href="javascript:void(0)">Trước</a>
    </li>
    <li
      class="page-item"
      v-for="page in totalPages"
      :key="page"
      :class="{ active: currentPage === page }"
      @click="goToPage(page)"
    >
      <a class="page-link" href="javascript:void(0)">{{ page }}</a>
    </li>
    <li
      class="page-item"
      :class="{ disabled: currentPage === totalPages }"
      @click="goToPage(currentPage + 1)"
    >
      <a class="page-link" href="javascript:void(0)">Sau</a>
    </li>
  </ul>
</div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from '@/axios' // axios có JWT interceptor
import Swal from 'sweetalert2'

const danhSachTinTuc = ref([])

const currentPage = ref(1)
const itemsPerPage = 5

const totalPages = computed(() =>
  Math.ceil(danhSachTinTuc.value.length / itemsPerPage)
)

const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  return danhSachTinTuc.value.slice(start, start + itemsPerPage)
})

const goToPage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

// Giải mã entity HTML (vd: &agrave; -> à)
const decodeHtmlEntities = (str) => {
  const parser = new DOMParser()
  return parser.parseFromString(str, 'text/html').body.textContent || ''
}

const formatNoiDung = (noiDung) => {
  const decoded = decodeHtmlEntities(noiDung)
  return decoded.length > 150 ? decoded.slice(0, 150) + '...' : decoded
}

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/news')
    danhSachTinTuc.value = res.data.map((item) => ({
      id: item.newsId,
      tieuDe: item.title,
      noiDung: item.content,
      hinhAnh: `http://localhost:8080${item.image}`,
      ngayDang: new Date(item.createdAt).toLocaleDateString('vi-VN')
    }))
  } catch (err) {
    Swal.fire({
      icon: 'error',
      title: 'Lỗi tải dữ liệu!',
      text: 'Không thể kết nối đến máy chủ hoặc dữ liệu không hợp lệ.'
    })
    console.error('Lỗi khi gọi API:', err)
  }
})
</script>


<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
}




/* Header */
.lien-he {
  font-size: 14px;
  font-family: Roboto;
  background: #fff;
  font-weight: 500;
  color: #000;
  width: 100vw;
}

.bread-crumb .title-bread-crumb {
  text-align: center;
  font-size: 50px;
  color: #cd9b32;
  font-weight: 600;
  font-family: 'Forte';
}

.breadcrumb {
  font-size: 14px;
  padding: 15px 10px;
  line-height: 24px;
  width: 100%;
  text-align: center;
  display: flex;
  align-items: center;
justify-content: center;
  list-style: none;
  margin: 0;
}

.breadcrumb .home a {
  text-decoration: none !important;
  color: white;
  font-size: 14px !important;
  text-align: center;
  display: inline-block;
}

.breadcrumb li {
  display: inline-block;
  margin: 0 10px;
  text-align: center;
}

.breadcrumb li:first-child {
  margin-left: 0;
}

.breadcrumb li:last-child {
  margin-right: 0;
}

.breadcrumb li .mr_lr svg {
  width: 10px;
  height: 10px;
  margin: 0 5px;
  vertical-align: middle;
}

.breadcrumb li a:hover span,
.breadcrumb li.active span,
.breadcrumb li strong span {
  color: #cd9b32;
}

.breadcrumb li > a > span,
.breadcrumb li span {
  color: #fff;
}

/* Quy tắc để đổi màu "Sản phẩm yêu thích" thành vàng */
.breadcrumb .home a:nth-child(2) span {
  color: #cd9b32 !important;
  font-size: 18px !important;
  text-align: center;
  display: inline-block;
}

.bread-crumb {
  margin-bottom: 40px;
  min-height: 200px;
  background-attachment: fixed;
  background-position: center center;
  background-repeat: no-repeat;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 150px 0 50px;
  text-align: center;
}

.bread-crumb .container {
  width: 100%;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

header.header {
  padding: 10px 0;
  position: absolute;
  top: 0 !important;
  left: 0;
  right: 0;
  width: 100%;
  z-index: 9;
  background-color: transparent;
}

.header .nav-link {
  color: white;
}

.nav-link li a:hover {
  color: #e4a53d;
}

.header .nav-link:hover {
  color: #e4a53d;
}

.abc {
  display: flex;
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
  flex-wrap: wrap;
}

main.abc {
  flex: 1;
  min-width: 65%;
}

/* Articles Grid */
.articles {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
  width: 100%;
}

article {
  position: relative;
  padding: 5px;
  border-radius: 5px;
  text-align: center;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

article:hover {
  transform: translateY(-5px);
}

article .date {
  position: absolute;
  top: 20px;
  left: 20px;
  background-color: #cd9b32;
  color: #f5ebd7;
  padding: 5px 10px;
  border-radius: 6px;
  font-size: 15px;
  font-weight: bold;
  z-index: 2;
}

/* Container cho ảnh */
.image-container {
  width: 100%; /* Đảm bảo container có kích thước cố định */
  height: 300px; /* Chiều cao cố định của khung ảnh */
  overflow: hidden; /* Ẩn phần ảnh vượt ra ngoài khung */
  border-radius: 5px 5px 0 0;
  position: relative;
}

article img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 5px 5px 0 0;
  margin-bottom: 10px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

/* Khi nhấp (checkbox được chọn), phóng to ảnh nhưng không vượt ra ngoài khung */
.zoom-checkbox:checked + label img {
transform: scale(1.5); /* Phóng to ảnh lên 1.5 lần */
}

/* Hiệu ứng hover (tùy chọn) */
article img:hover {
  transform: scale(1.1); /* Phóng to nhẹ khi hover */
}

article h3 {
  font-size: 20px;
  color: #000000;
  margin: 15px 0;
  position: relative;
  padding-bottom: 15px;
}

article h3::after {
  content: '';
  display: block;
  width: 70px;
  height: 3px;
  background-color: #cd9b32;
  margin: 10px auto 0;
  border-radius: 2px;
}

article p {
  font-size: 16px;
  color: #282828;
  line-height: 1.5;
  margin-bottom: 20px;
}

/* Sidebar */
.right-sidebar {
  width: 100%;
  max-width: 350px;
  margin-left: 30px;
}

.sidebar {
  background: #f3e7cd;
  color: #262521;
  border: 1px solid #ddd;
  padding: 25px;
  border-radius: 5px;
  margin-bottom: 30px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
}

.sidebar h2 {
  font-size: 22px;
  color: #d3a74b;
  margin-bottom: 20px;
  position: relative;
  padding-bottom: 10px;
}

.sidebar h2::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: 0;
  width: 50px;
  height: 2px;
  background-color: #cd9b32;
}

.sidebar ul {
  list-style: none;
  padding: 0;
}

.sidebar ul li {
  margin-bottom: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding-bottom: 8px;
}

.sidebar ul li:last-child {
  border-bottom: none;
}

.sidebar ul li a {
  text-decoration: none;
  color: #333;
  font-size: 16px;
  transition: color 0.3s ease;
}

.sidebar ul li a:hover {
  color: #cd9b32;
}

/* Highlighted News */
.highlight-news {
  list-style: none;
  padding: 0;
  margin: 0;
}

.highlight-news li {
  position: relative;
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  border-radius: 5px;
  padding-bottom: 15px;
  border-bottom: 1px dashed rgba(0, 0, 0, 0.1);
}

.highlight-news li:last-child {
  border-bottom: none;
}

.highlight-news a {
  display: flex;
  align-items: center;
  text-decoration: none;
  color: #333;
  width: 100%;
}

.highlight-news .number {
  position: absolute;
  top: 5px;
  left: 5px;
  z-index: 1;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: #e4a53d;
  color: #fff;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.highlight-news img {
  width: 80px;
  height: 60px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
}

.highlight-news .content {
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: calc(100% - 92px);
}

.highlight-news h3 {
  font-size: 14px;
  margin: 0 0 5px 0;
  color: #333;
  text-align: left;
}

.highlight-news h3::after {
  display: none;
}

.highlight-news .date {
  font-size: 12px;
  color: #2f403c;
  text-align: left;
}

/* Responsive Adjustments */
@media (max-width: 1200px) {
  .abc {
    flex-direction: column;
  }

  .right-sidebar {
    width: 100%;
    max-width: 100%;
    margin-left: 0;
    margin-top: 30px;
  }

  .articles {
    width: 100%;
    margin-left: 0;
  }
}
@media (max-width: 768px) {
  .articles {
    grid-template-columns: 1fr;
  }

  .bread-crumb .title-bread-crumb {
    font-size: 36px;
  }

  .breadcrumb li span {
    font-size: 16px;
  }
}
.bread-crumb .title-bread-crumb[data-v-503947e0] {
    text-align: center;
    font-size: 50px;
    color: #cd9b32;
    font-weight: 600;
    font-family: 'Playball';
}
</style>
