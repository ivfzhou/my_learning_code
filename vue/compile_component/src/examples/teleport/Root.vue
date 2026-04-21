<script setup>
import {ref} from 'vue'

const show = ref(false)
</script>

<template>
  <div>
    <button @click="show = !show">点击</button>
  </div>
  <Teleport defer to="body">
    <Transition>
      <div v-if="show">
        <p>Hello</p>
      </div>
    </Transition>
  </Teleport>
</template>

<style scoped>
.v-enter-from, .v-leave-to {
  opacity: 0;
}

.v-enter-active, .v-leave-active {
  transition: all .5s ease;
}
</style>
