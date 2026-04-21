<script setup>
import {useAttrs, watch} from 'vue'
import DeepChild from '@/examples/attrs_usage/DeepChild.vue'

const attrs = useAttrs()
console.log('first attrs', attrs)

watch(() => attrs, (newVal, oldVal) => {
  console.log('newVal attrs', newVal)
}, {deep: 1})
</script>

<template>
  <DeepChild/>
</template>

<style scoped>

</style>
