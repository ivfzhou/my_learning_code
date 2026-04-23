<script setup>
import Child from '@/examples/slot_selector/Child.vue'
</script>

<template>
  <div>
    <p>Parent</p>
    <Child>
      <p>Parent insert Content</p>
    </Child>
  </div>
</template>

<style scoped>
</style>
