<script setup>
import {ref} from 'vue'
import Child from '@/examples/model/Child.vue'
import Child2 from '@/examples/model/Child2.vue'
import Child3 from '@/examples/model/Child3.vue'

const value = ref('')
const value2 = ref('')
const value3 = ref(0)
</script>

<template>
  <div>
    set value: <input @change.self="value = $event.target.value"/><br/>
    <Child v-model:value="value"/>
  </div>
  <hr/>

  <div>
    set value2: <input @change.self="value2 = $event.target.value"/><br/>
    <Child2 v-model.modifier="value2"/>
  </div>
  <hr/>

  <div>
    set value3: <input @change.self="value3 = $event.target.value"/><br/>
    <Child3 v-model.number="value3"/>
  </div>
</template>

<style scoped>

</style>
