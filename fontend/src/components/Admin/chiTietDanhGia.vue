<template>
  <div class="container">
    <div class="page-header mb-4">
      <div class="header-breadcrumb-row">
        <h5 class="page-title mb-1">Chi tiết đánh giá</h5>
      </div>
    </div>

    <div v-if="loading" class="text-center py-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Đang tải...</span>
      </div>
      <p class="mt-2 text-muted">Đang tải dữ liệu, vui lòng chờ...</p>
    </div>

    <div v-if="errorMessage && !loading" class="alert alert-danger alert-dismissible fade show" role="alert">
      <i class="fas fa-exclamation-triangle me-2"></i>
      {{ errorMessage }}
      <button type="button" class="btn-close" @click="errorMessage = ''" aria-label="Close"></button>
    </div>

    <div v-if="rating && !loading" class="card shadow-lg border-0 rounded-4 p-4">
      <div class="card-header bg-white border-0 d-flex justify-content-between align-items-center mb-4">
        <h4 class="mb-0 text-dark fw-bold">Đánh giá #{{ rating.id }}</h4>
        <span :class="getStatusClass(rating.status)" class="fs-6 py-2 px-3 rounded-pill fw-bold">
          <i :class="getStatusIcon(rating.status)" class="me-1"></i>
          {{ rating.status || 'N/A' }}
        </span>
      </div>

      <div class="card-body p-4">
        <div class="row mb-4 border-bottom pb-3">
          <div class="col-md-6 mb-3 mb-md-0">
            <h5 class="fw-bold text-secondary"><i class="fas fa-user me-2"></i>Người đánh giá</h5>
            <p class="lead mb-0">{{ rating.userFullName || 'N/A' }}</p>
          </div>
          <div class="col-md-6">
            <h5 class="fw-bold text-secondary"><i class="fas fa-box me-2"></i>Sản phẩm</h5>
            <p class="lead mb-0">{{ rating.productName || 'N/A' }}</p>
          </div>
        </div>

        <div class="row mb-4 border-bottom pb-3">
          <div class="col-md-6 mb-3 mb-md-0">
            <h5 class="fw-bold text-secondary"><i class="fas fa-star me-2"></i>Số sao</h5>
            <p class="fs-4 fw-bold text-warning mb-0">{{ renderStars(rating.ratings) }}</p>
          </div>
          <div class="col-md-6">
            <h5 class="fw-bold text-secondary"><i class="fas fa-calendar-alt me-2"></i>Ngày đánh giá</h5>
            <p class="lead mb-0">{{ formatDate(rating.ratingDate) }}</p>
          </div>
        </div>

        <div class="row mb-4 border-bottom pb-3">
          <div class="col-12">
            <h5 class="fw-bold text-secondary"><i class="fas fa-comment-alt me-2"></i>Nội dung đánh giá</h5>
            <div class="bg-light p-4 rounded-3 mt-2">
              <p class="mb-0 fst-italic text-dark">{{ rating.comment || 'Không có nội dung' }}</p>
            </div>
          </div>
        </div>

        <div class="row" v-if="rating.reviewImages && rating.reviewImages.length">
          <div class="col-12">
            <h5 class="fw-bold text-secondary"><i class="fas fa-images me-2"></i>Ảnh đính kèm</h5>
            <div class="d-flex flex-wrap gap-3 mt-3">
              <div v-for="(image, index) in rating.reviewImages" :key="index" class="position-relative">
                <img
                  :src="getImageUrl(image.images)"
                  class="img-thumbnail border border-primary rounded-3 shadow-sm preview-image"
                  :alt="`Ảnh ${index + 1} của ${rating.productName}`"
                  @click="openLightbox(getImageUrl(image.images), index)"
                />
                <span class="position-absolute top-0 start-100 translate-middle badge bg-secondary">
                  {{ index + 1 }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="row">
          <div class="col-12">
            <h5 class="fw-bold text-secondary"><i class="fas fa-images me-2"></i>Ảnh đính kèm</h5>
            <p class="text-muted mt-2">Không có ảnh nào được đính kèm.</p>
          </div>
        </div>
      </div>

      <!-- Lightbox chuyên nghiệp -->
      <div v-if="showLightbox" class="lightbox-overlay" @click.self="closeLightbox">
        <div class="lightbox-container">
          <button class="lightbox-close-btn" @click="closeLightbox">
            <i class="bi bi-x-lg"></i>
          </button>

          <div class="lightbox-main">
            <button
              class="lightbox-arrow lightbox-prev"
              @click.stop="prevImage"
              :disabled="rating.reviewImages.length <= 1"
            >
              <i class="bi bi-chevron-left"></i>
            </button>

            <div class="lightbox-image-container">
              <img
                :src="currentImage"
                class="lightbox-image"
                :alt="`Ảnh ${currentImageIndex + 1} của ${rating.productName}`"
                @click.stop
              >
            </div>

            <button
              class="lightbox-arrow lightbox-next"
              @click.stop="nextImage"
              :disabled="rating.reviewImages.length <= 1"
            >
              <i class="bi bi-chevron-right"></i>
            </button>
          </div>

          <div class="lightbox-footer">
            <div class="lightbox-counter">
              {{ currentImageIndex + 1 }} / {{ rating.reviewImages.length }}
            </div>
            <div class="lightbox-thumbnails" v-if="rating.reviewImages.length > 1">
              <div
                v-for="(image, index) in rating.reviewImages"
                :key="index"
                class="thumbnail-item"
                :class="{ 'active': currentImageIndex === index }"
                @click="goToImage(index)"
              >
                <img
                  :src="getImageUrl(image.images)"
                  :alt="`Thumbnail ${index + 1}`"
                  class="thumbnail-image"
                >
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="card-footer bg-white border-0 text-end mt-4">
        <router-link to="/quanLyDanhGia" class="btn btn-warning ">
          Quay lại
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/axios'

export default {
  name: 'ChiTietDanhGia',
  data() {
    return {
      rating: null,
      errorMessage: '',
      loading: true,
      showLightbox: false,
      currentImage: '',
      currentImageIndex: 0
    };
  },
  mounted() {
    this.fetchRatingDetails();
    window.addEventListener('keydown', this.handleKeydown);
  },
  beforeUnmount() {
    window.removeEventListener('keydown', this.handleKeydown);
  },
  methods: {
    async fetchRatingDetails() {
      this.loading = true;
      this.errorMessage = '';

      try {
        const ratingId = this.$route.params.id;

        if (!ratingId || isNaN(ratingId)) {
          this.errorMessage = "ID đánh giá không hợp lệ.";
          this.loading = false;
          return;
        }

        const response = await api.get(`/api/ratings/${ratingId}`);

        if (response.status === 200 && response.data && response.data.success) {
          this.rating = response.data.data;
        } else if (response.data && response.data.message) {
          this.errorMessage = response.data.message;
        } else {
          this.errorMessage = `Không tìm thấy đánh giá (ID: ${ratingId}) hoặc lỗi server: ${response.status} - ${response.statusText}`;
        }
      } catch (error) {
        if (error.response) {
          if (error.response.status === 404) {
            this.errorMessage = "Không tìm thấy đánh giá này.";
          } else if (error.response.data?.message) {
            this.errorMessage = `Lỗi server: ${error.response.data.message}`;
          } else {
            this.errorMessage = `Lỗi server: ${error.response.status} - ${error.response.statusText}`;
          }
        } else if (error.request) {
          this.errorMessage = "Không thể kết nối đến server. Vui lòng kiểm tra kết nối mạng.";
        } else {
          this.errorMessage = `Có lỗi xảy ra: ${error.message}`;
        }
        console.error("Error fetching rating details:", error);
      } finally {
        this.loading = false;
      }
    },

    getImageUrl(imagePath) {
      if (!imagePath) return '';
      const correctedPath = imagePath.startsWith('/') ? imagePath : `/${imagePath}`;
      return `http://localhost:8080${correctedPath}`;
    },

    openLightbox(imageUrl, index = 0) {
      this.currentImage = imageUrl;
      this.currentImageIndex = index;
      this.showLightbox = true;
      document.body.style.overflow = 'hidden';
    },

    nextImage() {
      if (this.rating.reviewImages && this.rating.reviewImages.length > 0) {
        this.currentImageIndex = (this.currentImageIndex + 1) % this.rating.reviewImages.length;
        this.currentImage = this.getImageUrl(this.rating.reviewImages[this.currentImageIndex].images);
      }
    },

    prevImage() {
      if (this.rating.reviewImages && this.rating.reviewImages.length > 0) {
        this.currentImageIndex = (this.currentImageIndex - 1 + this.rating.reviewImages.length) % this.rating.reviewImages.length;
        this.currentImage = this.getImageUrl(this.rating.reviewImages[this.currentImageIndex].images);
      }
    },

    goToImage(index) {
      this.currentImageIndex = index;
      this.currentImage = this.getImageUrl(this.rating.reviewImages[index].images);
    },

    closeLightbox() {
      this.showLightbox = false;
      document.body.style.overflow = '';
      this.currentImageIndex = 0;
    },

    handleKeydown(e) {
      if (this.showLightbox) {
        if (e.key === 'Escape') {
          this.closeLightbox();
        } else if (e.key === 'ArrowRight') {
          this.nextImage();
        } else if (e.key === 'ArrowLeft') {
          this.prevImage();
        }
      }
    },

    renderStars(rating) {
      const validRating = typeof rating === 'number' ? Math.max(0, Math.min(rating, 5)) : 0;
      return '⭐'.repeat(validRating) + '☆'.repeat(5 - validRating);
    },

    formatDate(date) {
      if (!date || isNaN(new Date(date).getTime())) return 'N/A';
      const d = new Date(date);
      return d.toLocaleString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour12: false
      }).replace(/\//g, '-');
    },

    getStatusClass(status) {
      switch (status) {
        case 'Đã duyệt': return 'badge bg-success text-white';
        case 'Chờ duyệt': return 'badge bg-warning text-dark';
        case 'Ẩn': return 'badge bg-secondary text-white';
        default: return 'badge bg-light text-dark';
      }
    },

    getStatusIcon(status) {
      switch (status) {
        case 'Đã duyệt': return 'fas fa-check-circle';
        case 'Chờ duyệt': return 'fas fa-hourglass-half';
        case 'Ẩn': return 'fas fa-eye-slash';
        default: return 'fas fa-question-circle';
      }
    }
  }
};
</script>

<style scoped>
/* Base styles */
.container {
  background-color: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
}

.page-header {
  padding: 1rem 1.5rem;
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

.card {
  border: none;
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  background-color: #ffffff;
  border: 1px solid #e9ecef;
}

.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 0.75rem 2rem rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  padding-bottom: 0;
}

.card-footer {
  padding-top: 1.5rem;
}

.col-form-label {
  color: #6c757d;
}

.lead {
  font-size: 1.1rem;
}

/* Image styles */
.preview-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  cursor: pointer;
  transition: transform 0.2s;
  border: 1px solid #dee2e6;
}

.preview-image:hover {
  transform: scale(1.05);
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15);
}

/* Lightbox chuyên nghiệp */
.lightbox-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  backdrop-filter: blur(8px);
  animation: fadeIn 0.3s ease;
}

.lightbox-container {
  width: 70%;
  max-width: 1200px;
  height: 90vh;
  display: flex;
  flex-direction: column;
  background: #111;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 0, 0, 0.6);
}

.lightbox-main {
  flex: 1;
  display: flex;
  position: relative;
  background-color: #000;
}

.lightbox-image-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.lightbox-image {
  max-height: 100%;
  max-width: 100%;
  object-fit: contain;
  transition: transform 0.3s ease;
  animation: zoomIn 0.3s ease;
}

.lightbox-close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  border-radius: 50%;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lightbox-close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.lightbox-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 50px;
  height: 80px;
  background: rgba(98, 91, 91, 0.323);
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  z-index: 10;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.lightbox-arrow:hover {
  background: rgba(255, 255, 255, 0.2);
}

.lightbox-arrow:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.lightbox-prev {
  left: 15px;
  border-radius: 0 5px 5px 0;
}

.lightbox-next {
  right: 15px;
  border-radius: 5px 0 0 5px;
}

.lightbox-footer {
  padding: 15px;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.lightbox-counter {
  color: white;
  font-size: 0.9rem;
  margin-bottom: 10px;
}

.lightbox-thumbnails {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 5px 0;
  max-width: 100%;
}

.thumbnail-item {
  width: 60px;
  height: 60px;
  border: 2px solid transparent;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.thumbnail-item:hover {
  border-color: rgba(255, 255, 255, 0.5);
}

.thumbnail-item.active {
  border-color: #007bff;
}

.thumbnail-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}

.alert {
  margin-bottom: 1rem;
}

/* Animations */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes zoomIn {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

/* Responsive */
@media (max-width: 768px) {
  .lightbox-container {
    width: 95%;
    height: 85vh;
  }

  .lightbox-arrow {
    width: 40px;
    height: 60px;
    font-size: 1.2rem;
  }

  .thumbnail-item {
    width: 50px;
    height: 50px;
  }
}

@media (max-width: 576px) {
  .lightbox-container {
    width: 100%;
    height: 100vh;
    border-radius: 0;
  }

  .lightbox-arrow {
    width: 30px;
    height: 50px;
  }

  .thumbnail-item {
    width: 40px;
    height: 40px;
  }
}
</style>
