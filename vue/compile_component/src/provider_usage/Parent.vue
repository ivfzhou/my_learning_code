<script setup>
import {provide, readonly, ref} from 'vue'
import {message} from "@/provider_usage/keys.js";

import Child from '@/provider_usage/Child.vue'

const msg = ref('asd')
provide(message, readonly(msg))

</script>

<template>
  <div>
    {{ msg }}
    <input type="text" v-model="msg"/>
  </div>
  <Child/>
</template>

<style scoped>

</style>
