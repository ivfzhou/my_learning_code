<script setup>
import {ref} from 'vue'
import Child from '@/slot_uasge/Child.vue'

const slotName = ref('header')
</script>

<template>
  <Child>
    <template #header>
      header
    </template>

    <!-- v-slot="" 简写。默认内容插给默认插槽 -->
    <template #default>
      body
    </template>

    <template #footer>
      footer
    </template>
  </Child>

  <div>
    <input type="text" v-model="slotName"/>
  </div>
  <Child>
    <!-- 动态插槽 v-slot:[slotName] -->
    <template #[slotName]>dynamic slot</template>
  </Child>

  <!-- 获取子组件传递来的数据 v-slot="{ message }" -->
  <Child #default="{ message }">
    Child Message: {{ message }}
  </Child>

  <Child #default="{ message }">
    <!--
    <template #footer>
      {{ message }}
    </template>
    -->
  </Child>
  <Child>
    <template #default="{ message }"></template>

    <template #footer></template>
  </Child>
</template>

<style scoped>

</style>
