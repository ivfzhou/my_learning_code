<script setup>
const [model, modifiers] = defineModel({
  set(value) {
    console.log(`value is ${value}, modifiers is ${modifiers}`)
    return value+'asd'
  }
})

console.log(`model is ${model}, modifiers is ${JSON.stringify(modifiers)}`)
</script>
<template>
  <div>
    {{ model }}
  </div>
</template>
