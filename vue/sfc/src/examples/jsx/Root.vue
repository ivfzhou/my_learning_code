<script setup>
import {Node1, Node2, Node3, Node4, Node5, Node6, Node7} from '@/examples/jsx/Root.jsx'
</script>

<template>
  <component :is="Node1"></component>
  <hr/>
  <component :is="Node2"></component>
  <hr/>
  <component :is="Node3"></component>
  <hr/>
  <component :is="Node4"></component>
  <hr/>
  <component :is="Node5"></component>
  <hr/>
  <component :is="Node6">
    defualt
    <template #header="{key}">{{ key }}</template>
  </component>
  <hr/>
  <component :is="Node7"></component>
</template>

<style scoped>

</style>
