<script setup>
import {ref} from 'vue'

const value = ref('')
</script>

<template>
  <div>
    <input type="text" v-model="value"/>
  </div>
  <div>
    {{ value }}
  </div>
</template>

<style scoped>

</style>
