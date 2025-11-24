<script setup>
import thongTinCaNhan from '../../components/User/thongTinCaNhan.vue'
import header1 from '../../components/User/header.vue'
import footer1 from '../../components/User/footer.vue'
import layoutUser from '../../components/User/layoutUser.vue'
</script>
<template>
  <header>
    <header1 />
  </header>
  <main>
    <layoutUser><thongTinCaNhan /></layoutUser>
    
  </main>
  <footer>
    <footer1 />
  </footer>

</template>
