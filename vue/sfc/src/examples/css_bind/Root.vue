<script setup>
import {ref} from 'vue'

const color = ref('red')
</script>

<template>
  <div>
    set color: <input v-model="color"/><br/>
    <p class="a">Hello</p>
  </div>
</template>

<style scoped>
.a {
  color: v-bind(color)
}
</style>
