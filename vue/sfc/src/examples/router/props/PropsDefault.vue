<script setup>
defineProps({
  param: String,
})
</script>

<template>
  <div class="root">
    <p>PropsDefault</p>
    <p>param is {{ param }}</p>
  </div>
</template>

<style scoped>
.root {
  border: 2px solid black;
  margin: 2px;
}
</style>