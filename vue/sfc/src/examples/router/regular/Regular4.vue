<script setup>
import {useRoute} from 'vue-router'

const route = useRoute()
</script>

<template>
  <div class="root">
    <p>Regular 4</p>
    <p>id: {{ route.params.id }}</p>
  </div>
</template>

<style scoped>
.root {
  border: 2px solid black;
}
</style>
