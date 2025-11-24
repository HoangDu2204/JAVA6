<template>
  <div class="page-lien-he">
      <main>
      <!-- BREADCRUMB -->
      <section
        class="bread-crumb full-width-banner"
        :style="{ backgroundImage: 'url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436)' }"
      >
        <div class="banner-overlay"></div>
        <div class="container banner-content">
          <div class="title-bread-crumb">{{ baiViet?.tieuDe }}</div>
          <ul class="breadcrumb">
            <li class="home"><a href="/"><span>Trang chủ</span></a><span class="mr_lr">&nbsp;>&nbsp;</span></li>
            <li><a href="/tinTuc"><span>Tin tức</span></a><span class="mr_lr">&nbsp;>&nbsp;</span></li>
            <li><strong style="color: #f9b233">{{ baiViet?.tieuDe }}</strong></li>
          </ul>
        </div>
      </section>

     <!-- BODY -->
      <section class="blogpage">
        <div class="container layout-article">
          <div class="bg_blog">
            <article class="article-main">
              <div class="row">
                <!-- CHI TIẾT BÀI VIẾT -->
                <div class="right-content col-lg-8 col-12">

                  <div class="article-details clearfix">
                     <h1 class="article-title vietnamese-text">{{ baiViet?.tieuDe }}</h1>
                    <div class="posts mb-2">
                      <div class="time-post f vietnamese-text">Ngày đăng: {{ baiViet?.ngayDang }}</div>
                       <div class="rte article-content vietnamese-text" v-html="baiViet?.noiDung"></div>
                    </div>
                    <div class="mb-3" v-if="baiViet?.hinhAnh">
                      <img :src="baiViet.hinhAnh" :alt="baiViet.tieuDe" class="img-fluid rounded w-100" />
                    </div>

                    <!-- <div class="rte article-content" v-html="baiViet?.noiDung"></div> -->


                    <div class="thump-comment">
                      <form @submit.prevent="submitComment" id="article_comments">
                        <div class="form-coment">
                          <div class="margin-top-0 margin-bottom-30 w-100">
                            <h3 class="title-index vietnamese-text">
                              <span v-if="!replyingTo">Viết bình luận của bạn</span>
                              <span v-else>Phản hồi bình luận của {{ replyingTo.userFullName }}</span>
                            </h3>
                            <!-- Hiển thị thông báo đang reply -->
                            <div v-if="replyingTo" class="alert alert-info mb-3">
                              <div class="d-flex align-items-center">
                                <i class="bi bi-reply me-2"></i>
                                <span>Đang phản hồi bình luận của <strong>{{ replyingTo.userFullName }}</strong></span>
                                <button type="button" class="btn btn-sm btn-outline-secondary ms-auto" @click="cancelReply">
                                  <i class="bi bi-x"></i> Hủy
                                </button>
                              </div>
                            </div>
                            
                            <!-- Hiển thị thông tin người dùng đăng nhập -->
                            <div v-if="isLoggedIn && currentUser" class="alert alert-success mb-3">
                              <div class="d-flex align-items-center">
                                <i class="bi bi-person-check me-2"></i>
                                <span>Đăng nhập với tư cách: <strong>{{ currentUser.fullName || currentUser.name }}</strong></span>
                              </div>
                            </div>
                          </div>
                          <div class="row comment-form-container">
                            <div class="col-md-6 col-12">
                              <fieldset class="form-group padding-0">
                                <input
                                  v-model="commentForm.author"
                                  placeholder="Họ và tên"
                                  type="text"
                                  class="form-control form-control-lg"
                                  id="full-name"
                                  name="Author"
                                  required
                                  :readonly="isLoggedIn"
                                />
                              </fieldset>
                            </div>
                            <div class="col-md-6 col-12">
                              <fieldset class="form-group padding-0">
                                <input
                                  v-model="commentForm.email"
                                  placeholder="Email"
                                  type="email"
                                  class="form-control form-control-lg"
                                  name="Email"
                                  required
                                  :readonly="isLoggedIn"
                                />
                              </fieldset>
                            </div>
                            <fieldset class="form-group col-lg-12 col-md-12 col-sm-12 col-xs-12 textarea-containerx">
                              <textarea
                                v-model="commentForm.content"
                                :placeholder="replyingTo ? `Viết phản hồi cho ${replyingTo.userFullName}...` : 'Nội dung bình luận của bạn...'"
                                :class="['form-control form-control-lg', { 'border-primary': replyingTo }]"
                                id="comment"
                                name="Body"
                                rows="6"
                                required
                              ></textarea>
                              <div v-if="replyingTo" class="form-text text-primary">
                                <i class="bi bi-info-circle"></i> Bạn đang phản hồi bình luận của {{ replyingTo.userFullName }}
                              </div>
                            </fieldset>
                            <div class="col-12">
                              <button type="submit" class="btn btn-primary button_45">
                                {{ replyingTo ? 'Gửi phản hồi' : 'Gửi bình luận' }}
                              </button>
                              <button v-if="replyingTo" type="button" class="btn btn-secondary ms-2" @click="cancelReply">
                                Hủy
                              </button>
                            </div>
                          </div>
                        </div>
                      </form>

                      <!-- DANH SÁCH BÌNH LUẬN -->
                      <div id="article-comments">
                        <h5 class="title-form-coment margin-bottom-25 vietnamese-text">Bình luận ({{ (comments || []).filter(c => c.isVisible).length }})</h5>
                        
                        <!-- Loading state -->
                        <div v-if="isLoading" class="comment-loading">
                          <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Đang tải...</span>
                          </div>
                          <p class="mt-2">Đang tải bình luận...</p>
                        </div>
                        
                        <!-- Component hiển thị bình luận lồng -->
                        <div v-else-if="visibleComments.length > 0">
                          <div v-for="comment in visibleComments" :key="comment.id" class="comment-tree">
                            <CommentItem 
                              :comment="comment" 
                              :comments="comments"
                              @reply="startReply"
                              :depth="0"
                              :isLoggedIn="isLoggedIn"
                            />
                          </div>
                        </div>
                        
                        <!-- No comments state -->
                        <div v-else class="comment-empty">
                          <i class="bi bi-chat-dots"></i>
                          <h5>Chưa có bình luận nào</h5>
                          <p class="text-muted">Hãy là người đầu tiên bình luận về bài viết này!</p>
                        </div>
                        
                        <div class="text-center article_page section paginate_center"></div>
                      </div>
                    </div>
                  </div>
                </div>
  <!-- TIN TỨC LIÊN QUAN -->
                  <div v-if="tinLienQuan.length > 0" class="mt-5">
                    <h4 class="mb-3 vietnamese-text">Tin tức liên quan</h4>
                    <div class="row g-3">
                      <div class="col-md-6 col-lg-6" v-for="tin in tinLienQuan" :key="tin.id">
                        <div class="card h-100 shadow-sm border-0">
                          <a :href="`/binhLuanTinTuc/${tin.id}`">
                            <img
                              :src="tin.hinhAnh"
                              class="card-img-top"
                              :alt="tin.tieuDe"
                              style="height: 180px; object-fit: cover; border-top-left-radius: 10px; border-top-right-radius: 10px"
                            />
                          </a>
                          <div class="card-body">
                            <h5 class="card-title text-truncate" style="max-width: 100%">
                              <a :href="`/binhLuanTinTuc/${tin.id}`">{{ tin.tieuDe }}</a>
                            </h5>
                            <p class="card-text text-muted">{{ tin.ngayDang }}</p>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>



                <!-- SIDEBAR (CỘT BÊN PHẢI) -->
                <div class="blog_left_base col-lg-4 col-12">
                  <div class="aside-content-menu aside-content-blog">
                    <div class="title-head-col vietnamese-text">Danh mục tin tức</div>
                    <nav class="nav-category">
                      <ul class="nav navbar-pills flex-column">
                        <li class="nav-item"><a class="nav-link" href="/">Trang chủ</a></li>
                        <li class="nav-item"><a class="nav-link" href="/gioiThieu">Giới thiệu</a></li>
                        <li class="nav-item"><a class="nav-link" href="/sanPham">Sản phẩm</a></li>
                        <li class="nav-item"><a class="nav-link" href="/tinTuc">Tin tức</a></li>
                        <li class="nav-item"><a class="nav-link" href="/lienHe">Liên hệ</a></li>
                        <li class="nav-item"><a class="nav-link" href="/heThongCuaHang">Hệ thống cửa hàng</a></li>
                        <li class="nav-item"><a class="nav-link" href="/cauHoiThuongGap">Câu hỏi thường gặp</a></li>
                      </ul>
                    </nav>
                  </div>

                  <div class="blog_noibat mt-4">
                    <h2 class="h2_sidebar_blog vietnamese-text">Tin tức nổi bật</h2>
                    <div class="blog_content">
                      <div class="item clearfix" v-for="(tin, index) in tinNoiBat" :key="tin.id">
                        <div class="post-thumb">
                          <a class="image-blog scale_hover" :href="`/binhLuanTinTuc/${tin.id}`">
                            <img :src="tin.hinhAnh" :alt="tin.tieuDe" />
                          </a>
                          <span class="num">{{ index + 1 }}</span>
                        </div>
                        <div class="contentright">
                          <h3><a :href="`/binhLuanTinTuc/${tin.id}`">{{ tin.tieuDe }}</a></h3>
                          <div class="time-post">{{ tin.ngayDang }}</div>
                        </div>
                      </div>
                    </div>
                  </div>

                </div>
                 <div class="col-12 blog-lienquan order-lg-4">
                  <h3 class="title-index">
                    <a class="title-name" href="/tin-tuc">Tin liên quan </a>
                    <img
                      width="202"
                      height="20"
                      class="lazyload loaded"
                      src="//bizweb.dktcdn.net/100/492/035/themes/919334/assets/title.png?1735117293436"
                      data-src="//bizweb.dktcdn.net/100/492/035/themes/919334/assets/title.png?1735117293436"
                      alt="title"
                      data-was-processed="true"
                    />
                  </h3>
                  <div class="list-blogs related-blogs">
                    <div
                      class="blog-swiper swiper-container swiper-container-initialized swiper-container-horizontal swiper-container-pointer-events"
                      style="cursor: grab"
                    >
                      <div class="swiper-wrapper" style="transform: translate3d(0px, 0px, 0px)">
                        <div
                          class="swiper-slide swiper-slide-active"
                          style="width: 410px; margin-right: 20px"
                        >
                          <div class="item-blog">
                            <div class="block-thumb">
                              <a
                                class="thumb"
                                href="/donut-chi-tu-8k-tai-dola"
                                title="Donut chỉ từ 8k tại Dola"
                              >
                                <img
                                  class="lazyload loaded"
                                  src="https://bizweb.dktcdn.net/100/492/035/articles/243062617-6096832187058353-42980.png?v=1692242235353"
                                  data-src="https://bizweb.dktcdn.net/100/492/035/articles/243062617-6096832187058353-42980.png?v=1692242235353"
                                  alt="Donut chỉ từ 8k tại Dola"
                                  data-was-processed="true"
                                />
                              </a>

                              <div class="time-post">17/08/2023</div>
                            </div>
                            <div class="block-content">
                              <h3>
                                <a
                                  class="line-clamp line-clamp-1"
                                  href="/donut-chi-tu-8k-tai-dola"
                                  title="Donut chỉ từ 8k tại Dola"
                                  >Donut chỉ từ 8k tại Dola</a
                                >
                              </h3>
                              <p class="justify line-clamp line-clamp-3">
                                &nbsp;Nhắc đến bánh Donut, dân sành thưởng thức hẳn không còn xa lạ
                                gì với món ăn vặt rất phổ biến ở các nước phương Tây này. Dù có
                                nguồn...
                              </p>
                            </div>
                          </div>
                        </div>
                        <div
                          class="swiper-slide swiper-slide-next"
                          style="width: 410px; margin-right: 20px"
                        >
                          <div class="item-blog">
                            <div class="block-thumb">
                              <a
                                class="thumb"
                                href="/croissant-ngan-lop-da-dang-cach-thuong-thuc"
                                title="Croissant ngàn lớp - đa dạng cách thưởng thức"
                              >
                                <img
                                  class="lazyload loaded"
                                  src="https://bizweb.dktcdn.net/100/492/035/articles/banner-trang-chu-1rs-64a539e43a5.png?v=1692242153870"
                                  data-src="https://bizweb.dktcdn.net/100/492/035/articles/banner-trang-chu-1rs-64a539e43a5.png?v=1692242153870"
                                  alt="Croissant ngàn lớp - đa dạng cách thưởng thức"
                                  data-was-processed="true"
                                />
                              </a>

                              <div class="time-post">17/08/2023</div>
                            </div>
                            <div class="block-content">
                              <h3>
                                <a
                                  class="line-clamp line-clamp-1"
                                  href="/croissant-ngan-lop-da-dang-cach-thuong-thuc"
                                  title="Croissant ngàn lớp - đa dạng cách thưởng thức"
                                  >Croissant ngàn lớp - đa dạng cách thưởng thức</a
                                >
                              </h3>
                              <p class="justify line-clamp line-clamp-3">
                                &nbsp;Những chiếc bánh sừng bò với hương bơ thơm béo đặc trưng lại
                                còn đưa miệng với độ giòn xốp, dai dai từ "ngàn" lớp bánh. Nổi bật
                                với hình...
                              </p>
                            </div>
                          </div>
                        </div>
                        <div class="swiper-slide" style="width: 410px; margin-right: 20px">
                          <div class="item-blog">
                            <div class="block-thumb">
                              <a
                                class="thumb"
                                href="/banh-tart-thom-ngay-khong-the-bo-lo"
                                title="Bánh Tart thơm ngậy không thể bỏ lỡ"
                              >
                                <img
                                  class="lazyload loaded"
                                  src="https://bizweb.dktcdn.net/100/492/035/articles/244507007-6116411681767070-43207.png?v=1692242112913"
                                  data-src="https://bizweb.dktcdn.net/100/492/035/articles/244507007-6116411681767070-43207.png?v=1692242112913"
                                  alt="Bánh Tart thơm ngậy không thể bỏ lỡ"
                                  data-was-processed="true"
                                />
                              </a>

                              <div class="time-post">17/08/2023</div>
                            </div>
                            <div class="block-content">
                              <h3>
                                <a
                                  class="line-clamp line-clamp-1"
                                  href="/banh-tart-thom-ngay-khong-the-bo-lo"
                                  title="Bánh Tart thơm ngậy không thể bỏ lỡ"
                                  >Bánh Tart thơm ngậy không thể bỏ lỡ</a
                                >
                              </h3>
                              <p class="justify line-clamp line-clamp-3">
                                &nbsp;Tart trứng là loại bánh đường phố nổi tiếng ở Hong Kong được
                                rất nhiều người yêu thích. Không những thế, trong bảng xếp hạng 50
                                loại món ăn ngon...
                              </p>
                            </div>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </article>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import api from '@/axios';
import Swal from 'sweetalert2';
import CommentItem from './CommentItem.vue';
import '@/assets/nested-comments.css';

const route = useRoute();
const authStore = useAuthStore();
const newsId = parseInt(route.params.id);

const baiViet = ref(null);
const commentForm = ref({
  author: '',
  email: '',
  content: '',
  parentId: null // Thêm parentId để hỗ trợ bình luận lồng
});

const comments = ref([]);
const tinNoiBat = ref([]);
const tinLienQuan = ref([]);
const isLoading = ref(false);
const replyingTo = ref(null); // Để theo dõi bình luận đang reply

// Lấy thông tin user từ auth store
const currentUser = computed(() => authStore.user);
const isLoggedIn = computed(() => !!authStore.token);

// Hiển thị bình luận có cấu trúc lồng
const visibleComments = computed(() => {
  if (!comments.value || comments.value.length === 0) {
    return [];
  }
  
  const buildCommentTree = (comments, parentId = null) => {
    return comments
      .filter(comment => comment.parentCommentId === parentId && comment.isVisible === true)
      .map(comment => ({
        ...comment,
        id: comment.commentId, // Map commentId to id for consistency
        replies: buildCommentTree(comments, comment.commentId)
      }));
  };
  
  return buildCommentTree(comments.value);
});

// ====================== API ======================

const fetchChiTiet = async () => {
  try {
    const res = await api.get(`/api/news/${newsId}`);
    baiViet.value = {
      tieuDe: res.data.title,
      noiDung: res.data.content,
      hinhAnh: `http://localhost:8080${res.data.image}`,
      ngayDang: formatDate(res.data.createdAt)
    };
  } catch (err) {
    console.error('Lỗi lấy chi tiết bài viết:', err);
  }
};

// Cập nhật thông tin user vào form khi đăng nhập
const updateUserInfo = () => {
  if (currentUser.value) {
    commentForm.value.author = currentUser.value.fullName || currentUser.value.name || '';
    commentForm.value.email = currentUser.value.email || '';
  } else {
    // Reset form nếu không đăng nhập
    commentForm.value.author = '';
    commentForm.value.email = '';
  }
};

const fetchComments = async () => {
  isLoading.value = true;
  try {
    const response = await api.get(`/api/news-comments?newsId=${newsId}&onlyVisible=true`);
    
    // Handle 204 No Content response
    if (response.status === 204 || !response.data) {
      comments.value = [];
    } else {
      comments.value = response.data;
    }
  } catch (error) {
    console.error('Lỗi khi lấy bình luận:', error);
    comments.value = []; // Set empty array on error
  } finally {
    isLoading.value = false;
  }
};

const fetchTinNoiBat = async () => {
  try {
    const res = await api.get('/api/news');
    const ds = res.data.map(item => ({
      id: item.newsId,
      tieuDe: item.title,
      hinhAnh: `http://localhost:8080${item.image}`,
      ngayDang: formatDate(item.createdAt)
    }));
    tinNoiBat.value = ds.slice(0, 5);
  } catch (e) {
    console.error('Lỗi khi tải tin nổi bật:', e);
  }
};

const fetchTinLienQuan = async () => {
  try {
    const res = await api.get(`/api/news/${newsId}/related`);
    tinLienQuan.value = res.data.map(item => ({
      id: item.newsId,
      tieuDe: item.title,
      hinhAnh: `http://localhost:8080${item.image}`,
      ngayDang: formatDate(item.createdAt)
    }));
  } catch (e) {
    console.error('Lỗi khi lấy tin liên quan:', e);
  }
};

// ===================== Gửi Bình Luận =====================

// Hàm để bắt đầu reply một bình luận
const startReply = (comment) => {
  // Kiểm tra đăng nhập trước khi cho phép reply
  if (!isLoggedIn.value) {
    Swal.fire({
      icon: 'info',
      title: 'Yêu cầu đăng nhập',
      text: 'Bạn cần đăng nhập để phản hồi bình luận!',
      confirmButtonText: 'Đăng nhập'
    }).then(result => {
      if (result.isConfirmed) {
        window.location.href = '/dangNhap';
      }
    });
    return;
  }

  replyingTo.value = comment;
  commentForm.value.parentId = comment.commentId || comment.id;
  commentForm.value.content = `@${comment.userFullName} `;
  
  // Focus vào textarea
  setTimeout(() => {
    const textarea = document.querySelector('#comment');
    if (textarea) {
      textarea.focus();
      // Đặt con trỏ ở cuối text
      const length = textarea.value.length;
      textarea.setSelectionRange(length, length);
    }
  }, 100);
  
  // Scroll đến form bình luận
  const commentForm = document.getElementById('article_comments');
  if (commentForm) {
    commentForm.scrollIntoView({ behavior: 'smooth' });
  }
};

// Hàm để hủy reply
const cancelReply = () => {
  replyingTo.value = null;
  commentForm.value.parentId = null;
  commentForm.value.content = '';
};

const submitComment = async () => {
  // Kiểm tra user trước khi cho phép bình luận
  const isUserLoggedIn = isLoggedIn.value;
  if (!isUserLoggedIn) {
    await Swal.fire({
      icon: 'info',
      title: 'Yêu cầu đăng nhập',
      text: 'Bạn cần đăng nhập để thực hiện chức năng này!',
      confirmButtonText: 'Đăng nhập'
    }).then(result => {
      if (result.isConfirmed) {
        window.location.href = '/dangNhap';
      }
    });
    return;
  }

  // Kiểm tra thông tin user
  if (!currentUser.value) {
    await Swal.fire({
      icon: 'error',
      title: 'Lỗi thông tin người dùng',
      text: 'Không thể lấy thông tin người dùng. Vui lòng đăng nhập lại!',
      confirmButtonText: 'OK'
    });
    return;
  }

  if (!commentForm.value.content.trim()) {
    await Swal.fire({
      icon: 'warning',
      title: 'Thiếu nội dung',
      text: 'Vui lòng nhập nội dung bình luận.',
      confirmButtonText: 'OK'
    });
    return;
  }

  try {
    await api.post(
      '/api/news-comments',
      {
        newsId: newsId,
        content: commentForm.value.content,
        parentCommentId: commentForm.value.parentId || null // Gửi parentCommentId nếu là bình luận lồng
      },
      {
        withCredentials: true,
        headers: {
          'Content-Type': 'application/json'
        }
      }
    );

    commentForm.value.content = '';
    commentForm.value.parentId = null; // Đặt lại parentId khi gửi bình luận thành công
    replyingTo.value = null; // Đặt lại replyingTo
    await fetchComments();

    await Swal.fire({
      icon: 'success',
      title: 'Thành công',
      text: replyingTo.value ? 'Phản hồi đã được gửi!' : 'Bình luận đã được gửi!',
      timer: 2000,
      showConfirmButton: false
    });
  } catch (error) {
    console.error('Lỗi khi gửi bình luận:', error.response || error.message);

    if (error.response?.status === 401) {
      await Swal.fire({
        icon: 'error',
        title: 'Phiên đăng nhập hết hạn',
        text: 'Vui lòng đăng nhập lại!',
        confirmButtonText: 'OK'
      });
    } else {
      await Swal.fire({
        icon: 'error',
        title: 'Lỗi gửi bình luận',
        text: 'Vui lòng thử lại sau.',
        confirmButtonText: 'OK'
      });
    }
  }
};

// ====================== Tiện ích ======================

const formatDate = (dateString) => {
  if (!dateString) return 'N/A';
  try {
    return new Date(dateString).toLocaleString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch (e) {
    console.error('Lỗi định dạng ngày:', e);
    return 'N/A';
  }
};



// ====================== Mounted ======================

onMounted(async () => {
  // Cập nhật thông tin user từ auth store
  authStore.loadUserFromToken();
  updateUserInfo();
  
  await fetchChiTiet();
  await fetchComments();
  await fetchTinNoiBat();
  await fetchTinLienQuan();
});

// Watch for changes in user authentication
watch(() => authStore.user, (newUser) => {
  if (newUser) {
    updateUserInfo();
  }
}, { immediate: true });
</script>









<style scoped>
.blogpage {
  padding-top: 30px;
}
.layout-article {
  padding-bottom: 30px;
}
.d-none {
  display: none !important;
}
img {
  vertical-align: middle;
  border-style: none;
  max-width: 100%;
  height: auto;
}
article,
aside,
figcaption,
figure,
footer,
header,
hgroup,
main,
nav,
section {
  display: block;
}
.row {
  display: -ms-flexbox;
  display: flex;
  -ms-flex-wrap: wrap;
  flex-wrap: wrap;
  margin-right: -15px;
  margin-left: -15px;
}
@media (min-width: 992px) {
  .col-lg-8 {
    -ms-flex: 0 0 66.666667%;
    flex: 0 0 66.666667%;
    max-width: 66.666667%;
  }
}
.article-main .article-title {
  margin-top: 0px;
  margin-bottom: 15px;
  font-size: 40px;
  font-weight: 700;
  color: #000;
  line-height: 34px;
}
.article-main .posts {
  font-size: 20px;
  margin-bottom: 10px;
}
.article-main .time-post {
  font-size: 30px;
  font-weight: 400;
  color: #000;
  display: inline-block;
  margin-right: 10px;
}
.article-main .time-post svg {
  width: 15px;
  height: 15px;
  margin-top: -5px;
  margin-right: 2px;
}
*,
::after,
::before {
  box-sizing: border-box;
}
.article-main .rte {
  font-size: 18px;
}
p {
  margin-top: 0;
  margin-bottom: 1rem;
}
img {
  vertical-align: middle;
  border-style: none;
  max-width: 100%;
  height: auto;
}
.thump-comment {
  margin-top: 20px;
}
.article-main #article_comments {
  display: block;
  margin-top: 20px;
  margin-bottom: 20px;
}
.article-main #article_comments input {
  height: 40px;
}
.title-index {
  font-size: 50px;
  margin-bottom: 40px;
  padding-bottom: 5px;
  display: block;
  text-align: center;
  font-family: 'Roboto';
}
.title-index a,
.title-index span {
  display: block;
}
@media (min-width: 768px) {
  .col-md-6 {
    -ms-flex: 0 0 50%;
    flex: 0 0 50%;
    max-width: 50%;
  }
}
fieldset {
  min-width: 0;
  padding: 0;
  margin: 0;
  border: 0;
}
.article-main .form-group input {
  width: 100%;
  margin-bottom: 15px;
  outline: none;
  font-size: 18px;
  border-radius: 5px;
  padding: 5px 20px;
  border: 1px solid #e6e6e6;
  border-bottom: 2px solid #cd9b32;
}
@media (min-width: 992px) {
  .col-lg-12 {
    -ms-flex: 0 0 100%;
    flex: 0 0 100%;
    max-width: 100%;
  }
}
.article-main .form-group textarea {
  width: 100%;
  margin-bottom: 15px;
  outline: none;
  font-size: 18px;
  border-radius: 5px;
  padding: 5px 20px;
  border: 1px solid #e6e6e6;
  border-bottom: 2px solid #cd9b32;
}
.article-main .button_45 {
  height: 40px;
  line-height: 38px;
  padding: 0 25px;
  background: #cd9b32;
  font-size: 18px;
  color: #fff;
  border: solid 1px #cd9b32;
  font-weight: 400;
  margin-top: 10px;
  border-radius: 5px;
}
.article-main .button_45:hover {
  background: #fff;
  border-color: #e4a53d;
  color: #e4a53d;
}
/* bìnhluận */

.article-main .title-form-coment {
  font-size: 20px;
  margin-top: 0;
  margin-bottom: 5px;
  padding-bottom: 15px;
  color: #000;
  font-weight: 500;
}
#article-comments .article-comment:last-child {
  padding-bottom: 30px;
}
#article-comments .article-comment-user-image {
  margin: 0;
  float: left;
  width: 80px;
  margin-right: 12px;
  height: 80px;
  border-radius: 0;
  display: block;
}
#article-comments .article-comment-user-comment {
  display: block;
  float: left;
  width: calc(100% - 95px);
  -webkit-width: calc(100% - 95px);
  -moz-width: calc(100% - 95px);
  -o-width: calc(100% - 95px);
  -os-width: calc(100% - 95px);
  padding-left: 10px;
  word-break: break-word;
  font-size: em(18px);
}
#article-comments .user-name-comment {
  margin: 0;
  font-size: 18px;
  line-height: 1;
  font-weight: 700;
}
#article-comments .user-name-comment strong {
  font-weight: 700;
}
#article-comments .article-comment-date-bull {
  padding: 5px 0;
  display: block;
  color: #b0b0b0;
  font-size: 12px;
}
#article-comments .cm {
  font-size: 18px;
  color: #000;
}
@media (min-width: 992px) {
  .col-lg-4 {
    -ms-flex: 0 0 33.333333%;
    flex: 0 0 33.333333%;
    max-width: 33.333333%;
  }
}
.aside-content-blog {
  margin-bottom: 30px;
  background: #f3e7cd;
  padding: 5px 10px;
  border-radius: 5px;
}
.aside-content-blog .title-head-col {
  margin-bottom: 0;
  position: relative;
  font-size: 24px;
  font-weight: 700;
  color: #cd9b32;
}
.aside-content-blog .nav-category {
  padding: 5px;
  border-radius: 0 0 10px 10px;
}
.aside-content-blog .nav-category ul {
  padding: 0;
  margin: 0;
  list-style: none;
}
.relative {
  position: relative;
}
.aside-content-blog .nav-category ul .nav-item .nav-link {
  display: block;
  padding: 8px 0;
  font-size: 18px;
  color: #000;
}
.blog_noibat {
  margin-top: 30px;
  border-radius: 5px;
  padding: 10px;
  background: #f3e7cd;
}
.blog_noibat h2 {
  margin-bottom: 15px;
  position: relative;
  font-size: 24px;
  font-weight: 700;
  color: #cd9b32;
  display: inline-block;
}
.blog_noibat h2 a {
  color: #cd9b32;
}
.blog_noibat .blog_content {
  padding: 5px;
  border-radius: 0 0 10px 10px;
}
.blog_noibat .blog_content .item {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 20px;
}
.blog_noibat .blog_content .item .post-thumb {
  width: 90px;
  margin-right: 10px;
  position: relative;
}
a {
  transition: all 0.4s ease;
}
a {
  color: #231f20;
  text-decoration: none;
  background-color: transparent;
}
img.lazyload.loaded {
  height: auto;
}
.blog_noibat .blog_content .item .post-thumb .num {
  width: 20px;
  height: 20px;
  display: flex;
  background: #e4a53d;
  color: #fff;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  line-height: 20px;
  position: absolute;
  left: -8px;
  top: 50%;
  transform: translate(0, -50%);
}
.blog_noibat .blog_content .item .contentright {
  width: calc(100% - 100px);
}
.blog_noibat .blog_content .item .contentright h3 {
  font-size: 18px;
  margin: 0;
  font-weight: 700;
}
.blog_noibat .blog_content .item .contentright h3 a {
  display: block;
  color: #000;
}
.article-main .time-post {
  font-size: 17px;
  font-weight: 400;
  color: #000;
  display: inline-block;
  margin-right: 10px;
}
.blog-lienquan {
  margin-top: 50px;
}
.title-index {
  font-size: 50px;
  margin-bottom: 40px;
  padding-bottom: 5px;
  display: block;
  text-align: center;
  font-family: 'Roboto';
}
img.lazyload.loaded {
  height: auto;
}
.title-index img {
  display: block;
  margin: auto;
  max-height: 20px;
}
.swiper-container {
  margin-left: auto;
  margin-right: auto;
  position: relative;
  overflow: hidden;
  list-style: none;
  padding: 0;
  z-index: 1;
}
.swiper-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  z-index: 1;
  display: flex;
  transition-property: transform;
  box-sizing: content-box;
}
.swiper-slide {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
  position: relative;
  transition-property: transform;
}
.item-blog {
  overflow: hidden;
  transition: all 0.4s ease;
}
/* .item-blog .block-thumb {
    overflow: hidden;
    width: 100%;
    display: flex
;
    -o-justify-content: center;
    -moz-justify-content: center;
    -webkit-justify-content: center;
    -os-justify-content: center;
    -o-display: flex;
    -moz-display: flex;
    -webkit-display: flex;
    -os-display: flex;
    align-items: center;
    position: relative;
    height: auto !important;
    padding-bottom: 67%;
    background: #e4ca92;
    border-radius: 10px;
} */
img.lazyload.loaded {
  height: auto;
}
.item-blog .block-thumb .time-post {
  position: absolute;
  text-align: center;
  left: 5px;
  top: 5px;
  background: #cd9b32;
  border-radius: 5px;
  padding: 2px 10px;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}
.item-blog .block-content {
  padding: 10px 0;
  color: #000;
  width: 100%;
  text-align: center;
}
.item-blog .block-content h3 {
  font-size: 20px;
  font-weight: 700;
  position: relative;
}
.item-blog .block-content h3 a {
  color: #000;
}

/* header */
.page-lien-he {
  font-family: Roboto;
  background-color: #fff;
  overflow-x: hidden;
}

header.header-transparent {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 10;
  background-color: transparent;
  padding: 5px 0;
  transition: background-color 0.3s ease;
}

header.header-transparent .navbar {
  padding: 0;
}

header.header-transparent .navbar-brand img {
  max-height: 60px;
}

header.header-transparent .navbar-nav .nav-link {
  color: #fff;
  font-weight: 500;
  padding: 10px 15px;
  text-transform: none;
  font-size: 18px;
  position: relative;
  background-color: transparent !important;
  border-bottom: none !important;
  border-radius: 0 !important;
}

header.header-transparent .navbar-nav .nav-link.active {
  color: #cd9b32;
  font-weight: 700;
}

header.header-transparent .navbar-nav .nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 15px;
  right: 15px;
  height: 2px;
  background-color: #cd9b32;
}

header.header-transparent .navbar-nav .nav-link:hover {
  color: #cd9b32;
}

header.header-transparent .navbar-icons .nav-link {
  color: #fff;
  font-size: 23px;
  padding: 10px 8px;
}
header.header-transparent .navbar-icons .nav-link:hover {
  color: #cd9b32;
}

.navbar-toggler {
  border-color: rgba(255, 255, 255, 0.5);
}
.navbar-toggler-icon {
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 30 30'%3e%3cpath stroke='rgba%28255, 255, 255, 0.8%29' stroke-linecap='round' stroke-miterlimit='10' stroke-width='2' d='M4 7h22M4 15h22M4 23h22'/%3e%3c/svg%3e");
}

.full-width-banner {
  width: 100vw;
  position: relative;
  left: 50%;
  transform: translateX(-50%);

  min-height: 300px;
  background-size: cover;
  background-position: center center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 0 50px;
  text-align: center;
  margin-top: 0;
  margin-bottom: 0;
  box-sizing: border-box;
}

.banner-overlay {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
}

.full-width-banner .title-bread-crumb {
  font-size: 55px;
  color: #cd9b32;
  font-weight: normal;
  margin-bottom: 5px;
  font-family: 'Forte', cursive;
}

.full-width-banner .breadcrumb {
  font-size: 18px;
  padding: 0;
  width: auto;
  text-align: center;
  display: inline-block;
  background: none;
  margin: 0;
}

.full-width-banner .breadcrumb li {
  display: inline-block;
  margin: 0 3px;
}
.full-width-banner .breadcrumb li a {
  font-size: 14px;
  color: #eee;
  text-decoration: none;
}
.full-width-banner .breadcrumb li span {
  font-size: 18px;
  color: #eee;
}
.full-width-banner .breadcrumb li .mr_lr svg {
  width: 7px;
  height: 7px;
  margin: 0 5px;
  fill: #eee;
}
.full-width-banner .breadcrumb li strong span {
  color: #cd9b32;
  font-weight: 600;
}
.full-width-banner .breadcrumb li a:hover span {
  color: #fff;
}

.aside-content-blog .nav-category {
  padding: 5px;
  border-radius: 0 0 10px 10px;
}
.aside-content-blog .nav-category ul {
  padding: 0;
  margin: 0;
  list-style: none;
}
.nav-category .nav-link:hover {
  color: #cd9b32 !important;
  transition: color 0.3s ease;
}
.form-group.padding-0 {
  padding: 0;
  margin-bottom: 15px;
}

.form-control {
  border-top: none;
  border-left: none;
  border-right: none;
  border-bottom: 1px solid #cd9b32;
  border-radius: 0;
  padding: 10px 0;
}

.article-details {
  text-align: left !important;
}

/* Sidebar Menu */
.aside-content-menu,
.aside-content-menu .title-head-col,
.aside-content-menu .nav-link {
  text-align: left !important;
}

.blog_noibat,
.blog_noibat .h2_sidebar_blog,
.blog_noibat .h2_sidebar_blog a,
.blog_noibat .contentright,
.blog_noibat .contentright h3 a,
.blog_noibat .contentright .time-post {
  text-align: left !important;
  margin-left: 0; /* Reset potential auto margins */
  margin-right: 0;
}

#article-comments,
#article-comments .title-form-coment,
#article-comments .article-comment-user-comment,
#article-comments .article-comment-user-comment
{
  text-align: left !important;
  margin-left: 0; /* Reset potential auto margins */
  margin-right: 0;
}

.button_45{
  text-align: left !important;
}
.form-coment .col-12 {
    text-align: left !important;
}

/* Success state styling */
.form-control.is-valid {
  border-color: #198754;
  box-shadow: 0 0 0 0.2rem rgba(25, 135, 84, 0.25);
}

.form-control.is-valid:focus {
  border-color: #198754;
  box-shadow: 0 0 0 0.2rem rgba(25, 135, 84, 0.25);
}

/* CSS cho bình luận lồng */
.comment-tree {
  margin-bottom: 20px;
}

.article-comment {
  border-left: 3px solid transparent;
  transition: all 0.3s ease;
  margin-bottom: 15px;
  padding: 15px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.article-comment:hover {
  border-left-color: #007bff;
  background-color: #f8f9fa;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
}

.comment-replies {
  border-left: 2px solid #e9ecef;
  padding-left: 15px;
  margin-top: 10px;
}

.reply-item {
  margin-bottom: 10px;
}

.comment-actions {
  opacity: 0.7;
  transition: opacity 0.3s ease;
}

.article-comment:hover .comment-actions {
  opacity: 1;
}

.comment-actions .btn {
  font-size: 0.875rem;
  padding: 0.25rem 0.5rem;
}

/* Styling cho alert reply */
.alert-info {
  border-left: 4px solid #17a2b8;
  background-color: #d1ecf1;
  border-color: #bee5eb;
  color: #0c5460;
}

.alert-info .btn-outline-secondary {
  border-color: #6c757d;
  color: #6c757d;
}

.alert-info .btn-outline-secondary:hover {
  background-color: #6c757d;
  color: #fff;
}

/* Alert styling cho reply notification */
.alert-info {
  background-color: #d1ecf1;
  border-color: #bee5eb;
  color: #0c5460;
  border-radius: 8px;
  padding: 12px 16px;
}

.alert-info i {
  margin-right: 8px;
}

/* Responsive cho bình luận */
@media (max-width: 768px) {
  .article-comment {
    margin-left: 10px !important;
  }
  
  .comment-replies {
    padding-left: 10px;
  }
  
  .comment-actions .btn {
    font-size: 0.8rem;
    padding: 0.2rem 0.4rem;
  }
}

</style>
