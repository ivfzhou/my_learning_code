<script setup>
import {useRoute} from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="root">
    <p>NamingViewRightSidebar</p>
    <p>fullPath: {{ route.fullPath }}</p>
  </div>
</template>

<style scoped>
.root {
  border: 2px solid black;
  margin: 2px;
}
</style>
