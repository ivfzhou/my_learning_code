<script setup>
const props = defineProps(['title'])

defineExpose({
  a: 'a'
})
</script>

<template>
  <ChildComponent @fn="">slot content</ChildComponent>
</template>

<!-- ChildComponent -->
<script setup>
const emitObj = defineEmits(['fn'])
</script>
<template>
  <div @click="$emit('fn')"></div>
  <slot></slot>
</template>
