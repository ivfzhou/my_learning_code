<script setup>
const model = defineModel('value', {
  default: 'default value',
  required: true
})
</script>

<template>
  {{ model }}
</template>

<style scoped>

</style>
