<template>
  <div class="store-info-user">
    <img v-if="info.logoUrl" :src="info.logoUrl" alt="Logo" class="logo" />
    <h2>{{ info.storeName }}</h2>
    <div class="desc">{{ info.description }}</div>
    <div class="contact">
      <div><b>Địa chỉ:</b> {{ info.address }}</div>
      <div><b>Điện thoại:</b> {{ info.phone }}</div>
      <div><b>Email:</b> {{ info.email }}</div>
      <div><b>Giờ mở cửa:</b> {{ info.openTime }} - {{ info.closeTime }}</div>
    </div>
    <div class="social">
      <a v-if="info.facebookUrl" :href="info.facebookUrl" target="_blank">Facebook</a>
      <a v-if="info.zaloUrl" :href="info.zaloUrl" target="_blank">Zalo</a>
      <a v-if="info.instagramUrl" :href="info.instagramUrl" target="_blank">Instagram</a>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import axios from '@/axios' // ✅ Đã đổi sang axios có JWT interceptor

const info = ref({});
let intervalId = null;

const fetchStoreInfo = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/store-info');
    info.value = res.data || {};
  } catch (err) {
    console.error('Lỗi khi lấy thông tin cửa hàng:', err);
    info.value = {};
  }
};

onMounted(() => {
  fetchStoreInfo();
  intervalId = setInterval(fetchStoreInfo, 30000); // 30 giây
});
onUnmounted(() => {
  if (intervalId) clearInterval(intervalId);
});
</script>

<style scoped>
.store-info-user {
  max-width: 500px;
  margin: 0 auto;
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  text-align: center;
}
.logo {
  max-width: 120px;
  margin-bottom: 16px;
}
.social a {
  margin: 0 8px;
  color: #1976d2;
  text-decoration: none;
}
.social a:hover {
  text-decoration: underline;
}

</style>
