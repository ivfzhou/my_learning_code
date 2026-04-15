<script setup>
import {ref} from 'vue'
import Child from './Child.vue'

const content = ref('')

</script>

<template>
  <div>
    <input type="text" v-model="content"/>
  </div>
  <Child title="title" :content="content"/>
</template>

<style scoped>

</style>
