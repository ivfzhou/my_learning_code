<script setup>
import {onBeforeRouteUpdate, useRoute} from 'vue-router'

const route = useRoute()

onBeforeRouteUpdate((to, from) => {
  console.log(to, from)
})
</script>

<template>
  <div>
    <p>User route params is {{ route.params }}</p>
  </div>
</template>

<style scoped>

</style>
