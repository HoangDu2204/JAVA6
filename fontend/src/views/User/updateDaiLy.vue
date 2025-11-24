<script setup>
import updateDaiLy from '../../components/User/updateDaiLy.vue'
import header1 from '../../components/User/header.vue'
import footer1 from '../../components/User/footer.vue'
import layoutUser from '../../components/User/layoutUser.vue'
</script>
<template>
  <header>
    <header1 />
  </header>
  <main>
    <layoutUser>
      <updateDaiLy />
    </layoutUser>

  </main>
  <footer>
    <footer1 />
  </footer>

</template>
