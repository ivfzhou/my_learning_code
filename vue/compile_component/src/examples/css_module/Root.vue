<script setup>
import {useCssModule} from "vue"

const style = useCssModule('name')
</script>

<template>
  <div :class="name.p">
    Hello
  </div>
  <p :class="style.a">Hello</p>
</template>

<style module="name">
.a {
  color: blue;
}

.p {
  color: aquamarine;
}
</style>
