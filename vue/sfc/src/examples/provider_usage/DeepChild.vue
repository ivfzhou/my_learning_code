<script setup>
import {inject, ref} from 'vue'
import {message} from "@/examples/provider_usage/keys.js";

const msg = inject(message)
const msg2 = inject(message, () => ref('default message2'), true)

</script>

<template>
  <div>
    DeepChild：{{ msg }}
    <br/>
    DeepChild：{{ msg2 }}
  </div>
</template>

<style scoped>

</style>
