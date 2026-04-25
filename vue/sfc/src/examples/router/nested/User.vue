<script setup>
import {useRoute} from 'vue-router'

const route = useRoute()
</script>

<template>
  <div>
    <p>User route.fullPath is {{ route.fullPath }}</p>
    <RouterView/>
  </div>
</template>

<style scoped>

</style>
