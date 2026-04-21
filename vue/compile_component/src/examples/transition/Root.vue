<script setup>
import {ref} from 'vue'

const show = ref(false)

function onBeforeEnter(el) {

}

const count = ref(0)
setInterval(() => {
  count.value++
}, 1000)
</script>

<template>
  <button @click="show = !show">点击</button>
  <Transition name="name" enter-active-class="abc" type="animation" :duration="{enter: 1000, leave: 1000}"
              @before-enter="onBeforeEnter" :css="false" appear>
    <div v-if="show">
      Hello
    </div>
  </Transition>
  <Transition name="count">
    <span :key="count"> {{ count }} </span>
  </Transition>
</template>

<style scoped>
/*.name-enter-from {
  transform: translateX(10px);
  font-size: 2em;
}

.name-enter-active {
  transition: font-size 3s ease, transform 5s ease;
}

.name-enter-to {
  font-size: 1em;
}

.name-leave-from {
  font-size: 1em;
}

.name-leave-active {
  transition: font-size 3s ease;
}

.name-leave-to {
  font-size: 2em;
}*/

@keyframes bounce-in {
  0% {
    transform: scale(0);
    color: red;
  }
  50% {
    transform: scale(1.25);
  }
  100% {
    transform: scale(1);
  }
}

.abc {
  animation: bounce-in .5s;
}

.name-leave-active {
  animation: bounce-in .5s reverse;
}

.count-enter-active {
  animation: bounce-in 2s;
}
</style>
