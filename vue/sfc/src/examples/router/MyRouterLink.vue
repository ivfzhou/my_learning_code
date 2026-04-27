<script setup>
import {RouterLink, useLink} from 'vue-router'

const props = defineProps({
  ...RouterLink.props
})
const link = useLink(props)
</script>

<template>
  <a @click="link.navigate()">
    <slot/>
  </a>
</template>

<style scoped>

</style>
