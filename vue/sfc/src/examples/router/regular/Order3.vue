<script setup>
import {useRoute} from 'vue-router'

const route = useRoute()
</script>

<template>
  <div>
    <p>Order3 route params is {{ route.params }}</p>
  </div>
</template>

<style scoped>

</style>
