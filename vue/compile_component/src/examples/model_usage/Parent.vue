<script setup>
import {ref} from 'vue'
import Child from '@/examples/model_usage/Child.vue'

const value = ref('')
</script>

<template>
  <div>
    <div>
      <input type="text" v-model="value"/>
    </div>
    <Child v-model.customModifier="value"/>
  </div>
</template>
