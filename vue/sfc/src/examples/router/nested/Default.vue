<script setup>
import {useRoute} from 'vue-router'

const route = useRoute()
</script>

<template>
  <div>
    <p>Default route.fullPath is {{ route.fullPath }}</p>
  </div>
</template>

<style scoped>

</style>
