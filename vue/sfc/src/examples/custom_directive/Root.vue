<script setup>
import {ref} from 'vue'

const arg = ref('')

// 在模板中启用 v-highlight
const vHighlight = {
  mounted: (el, binding) => {
    console.log(binding.value, binding.oldValue, binding.arg, binding.modifiers)
  },
  updated: (el, binding) => {
    console.log(binding.value, binding.oldValue, binding.arg, binding.modifiers)
  }
}

const vHighlight2 = (el, binding) => {
  // 这会在 `mounted` 和 `updated` 时都调用
  el.style.color = binding.arg
}
</script>

<template>
  <input v-model="arg"/>
  <p v-highlight2:[arg].modify="123">This sentence is important!</p>
</template>

<style scoped>

</style>
