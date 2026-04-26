<script setup>
import {onBeforeRouteLeave, onBeforeRouteUpdate, useRoute} from 'vue-router'

const route = useRoute()

onBeforeRouteUpdate((to, from) => {
  console.log('onBeforeRouteUpdate', from.path, to.path)
})

onBeforeRouteLeave((to, from) => {
  console.log('onBeforeRouteLeave', from.path, to.path)
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
