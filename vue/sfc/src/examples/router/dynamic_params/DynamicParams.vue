<script setup>
import {onBeforeRouteUpdate, useRoute} from 'vue-router'

const route = useRoute()

onBeforeRouteUpdate((to, from) => {
  console.log('onBeforeRouteUpdate to', to, from)
  console.log('onBeforeRouteUpdate from', from)
})
</script>

<template>
  <div class="root">
    <p>DynamicParams</p>
    <p>id: {{ route.params.id }}, name: {{ route.params.name }}</p>
  </div>
</template>

<style scoped>
.root {
  border: 2px solid black;
}
</style>
