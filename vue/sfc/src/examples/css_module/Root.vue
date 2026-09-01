<script setup>
import {useCssModule} from "vue"

const customStyleName = useCssModule('name')
const defaultStyleName = useCssModule()
</script>

<template>
  <p :class="customStyleName.a">Hello</p>
  <p :class="name.b">Hello</p>
  <p :class="$style.c">Hello</p>
  <p :class="defaultStyleName.c">Hello</p>
</template>

<style module="name">
.a {
  color: blue;
}

.b {
  color: aquamarine;
}
</style>

<style module>
.c {
  color: red;
}
</style>
