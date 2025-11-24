<script setup>
import doiMatKhau from '../../components/User/doiMatKhau.vue'
import header1 from '../../components/User/header.vue'
import footer1 from '../../components/User/footer.vue'
import layoutUser from '../../components/User/layoutUser.vue'
</script>
<template>
  <header>
    <header1 />
  </header>
  <main>
    <!-- <doiMatKhau /> -->
     <layoutUser>
    <doiMatKhau />
  </layoutUser>
  </main>
  <footer>
    <footer1 />
  </footer>

</template>
