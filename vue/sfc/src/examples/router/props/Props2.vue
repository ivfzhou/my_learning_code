<script setup>
defineProps({
  propA: String,
  propB: Boolean,
})
</script>

<template>
  <div>
    <p>Root2</p>
    <p>propA: {{ propA }}, propB: {{ propB }}</p>
  </div>
</template>

<style scoped>

</style>