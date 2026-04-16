<script setup>
import {shallowRef} from 'vue'
import ComponentA from '@/keep_alive_usage/ComponentA.vue'
import ComponentB from '@/keep_alive_usage/ComponentB.vue'

let currentComponent = shallowRef(ComponentA)
</script>

<template>
  <div>
    ComponentA <input type="radio" checked="checked" name="radio" @click="currentComponent = ComponentA"/>
    ComponentB <input type="radio" name="radio" @click="currentComponent = ComponentB"/>
  </div>
  <div>
    <KeepAlive>
      <component :is="currentComponent"/>
    </KeepAlive>
  </div>
</template>

<style scoped>

</style>
