<script setup>
import {useSlots} from 'vue'

const slots = useSlots()
console.log(`slots: ${slots.header}`)
</script>

<template>
  <div>
    <div v-if="$slots.header">header slot provided</div>

    <slot name="header"></slot>

    <!-- <slot name="body"></slot> -->
    <slot message="hello"></slot>

    <slot name="footer"></slot>
  </div>
</template>

<style scoped>

</style>
