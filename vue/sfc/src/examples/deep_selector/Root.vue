<script setup>
import Child from "@/examples/deep_selector/Child.vue"
</script>

<template>
  <div class="a">
    Root
    <Child/>
    <p class="b">P</p>
  </div>
</template>

<style scoped>
.a :deep(.b) {
  color: red;
}
</style>
