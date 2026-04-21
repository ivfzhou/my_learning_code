<script setup>
import {ref} from 'vue'

const color = ref('red')
</script>

<template>
  <div>
    <div>
      <input type="text" v-model="color"/>
    </div>
    <p class="a b">Hello</p>
  </div>
</template>

<style scoped>
.a {
  color: v-bind(color)
}
</style>
