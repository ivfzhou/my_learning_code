<script setup>
import {onActivated, onDeactivated, ref} from 'vue'

const count = ref(0)

onActivated(() => {
  // 调用时机为首次挂载
  // 以及每次从缓存中被重新插入时
  console.log('onActivated')
})

onDeactivated(() => {
  // 在从 DOM 上移除、进入缓存
  // 以及组件卸载时调用
  console.log('onDeactivated')
})
</script>

<template>
  <div>
    <button @click="count++">点击</button>
  </div>
  <div>
    {{ count }}
  </div>
</template>

<style scoped>

</style>
