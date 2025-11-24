<script setup>
import { RouterLink, RouterView } from 'vue-router'
import HelloWorld from './components/HelloWorld.vue'
import ChatbotWrapper from './components/ChatbotWrapper.vue'
</script>

<template>
  <div class="container-fluid vw-100">


      <RouterView />
         <ChatbotWrapper />

  </div>

</template>

<style >
.container-fluid{
  padding-left: 0px;
  padding-right: 0px;

}

</style>
